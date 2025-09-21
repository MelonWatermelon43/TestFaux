package net.mat0u5.lifeseries.seasons.subin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;

import java.util.Optional;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class SubInCommands extends Command {
    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() != Seasons.UNASSIGNED;
    }

    @Override
    public Text getBannedText() {
        return Text.of("This command is only available when you have selected a Season.");
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("subin")
                .requires(PermissionManager::isAdmin)
                    .then(literal("add")
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("subin", StringArgumentType.string())
                                        .executes(context -> addSubIn(
                                                context.getSource(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                StringArgumentType.getString(context, "subin"))
                                        )
                                )
                        )
                    )
                    .then(literal("remove")
                            .then(argument("player", EntityArgumentType.player())
                                    .executes(context -> removeSubIn(
                                            context.getSource(),
                                            EntityArgumentType.getPlayer(context, "player"))
                                    )
                            )
                    )
                    .then(literal("list")
                            .executes(context -> listSubIns(context.getSource()))
                    )
        );
    }

    public int addSubIn(ServerCommandSource source, ServerPlayerEntity player, String target) {
        if (checkBanned(source)) return -1;

        GameProfile targetProfile = null;
        if (source.getServer().getUserCache() != null) {
            Optional<GameProfile> opt = source.getServer().getUserCache().findByName(target);
            if (opt.isPresent()) {
                targetProfile = opt.get();
            }
        }
        if (targetProfile == null) {
            source.sendError(Text.of("Failed to fetch target profile"));
            source.sendError(Text.of("Make sure the target player has logged on the server at least once"));
            return -1;
        }

        ServerPlayerEntity targetPlayer = PlayerUtils.getPlayer(target);
        if (targetPlayer != null) {
            source.sendError(Text.of("Online players cannot be subbed in for"));
            return -1;
        }

        if (SubInManager.isSubbingIn(player.getUuid())) {
            GameProfile profile = SubInManager.getSubstitutedPlayer(player.getUuid());
            source.sendError(TextUtils.formatPlain("{} is already subbing in for {}", player, profile.getName()));
            return -1;
        }

        if (SubInManager.isBeingSubstituted(targetProfile.getId())) {
            GameProfile profile = SubInManager.getSubstitutingPlayer(targetProfile.getId());
            source.sendError(TextUtils.formatPlain("{} is already being subbed in for by {}", target, profile.getName()));
            return -1;
        }

        OtherUtils.sendCommandFeedback(source, TextUtils.format("{} is now subbing in for {}", player, target));
        SubInManager.addSubIn(player, targetProfile);

        return 1;
    }

    public int removeSubIn(ServerCommandSource source, ServerPlayerEntity player) {
        if (checkBanned(source)) return -1;

        if (!SubInManager.isSubbingIn(player.getUuid())) {
            source.sendError(TextUtils.formatPlain("{} is not subbing in for anyone", player));
            return -1;
        }

        GameProfile profile = SubInManager.getSubstitutedPlayer(player.getUuid());

        OtherUtils.sendCommandFeedback(source, TextUtils.format("{} is no longer subbing in for {}", player, profile.getName()));
        SubInManager.removeSubIn(player);
        return 1;
    }

    public int listSubIns(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        if (SubInManager.subIns.isEmpty()) {
            source.sendError(Text.of("There are no sub ins yet"));
            return -1;
        }

        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7Current sub ins:"));

        for (SubInManager.SubIn subIn : SubInManager.subIns) {
            OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.formatLoosely(" §7{} is subbinng in for {}", subIn.substituter().getName(), subIn.target().getName()));
        }

        return 1;
    }
}
