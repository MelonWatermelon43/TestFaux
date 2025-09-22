package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.*;

public class ClaimKillCommand extends Command {

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
            literal("claimkill")
                .then(argument("player", EntityArgumentType.player())
                    .suggests((context, builder) -> CommandSource.suggestMatching(getSuggestions(), builder))
                    .executes(context -> claimCredit(
                        context.getSource(), EntityArgumentType.getPlayer(context, "player")
                    ))
                )
                .then(literal("validate")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("killer", EntityArgumentType.player())
                        .then(argument("victim", EntityArgumentType.player())
                            .executes(context -> claimCreditAccept(
                                context.getSource(),
                                EntityArgumentType.getPlayer(context, "killer"),
                                EntityArgumentType.getPlayer(context, "victim")
                            ))
                        )
                    )
                )
        );
    }

    public List<String> getSuggestions() {
        if (server == null) return new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        Set<UUID> recentDeaths = currentSession.playerNaturalDeathLog.keySet();
        for (UUID uuid : recentDeaths) {
            ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            suggestions.add(player.getNameForScoreboard());
        }
        return suggestions;
    }

    public int claimCredit(ServerCommandSource source, ServerPlayerEntity victim) {
        if (checkBanned(source)) return -1;
        if (victim == null) return -1;
        PlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        Set<UUID> recentDeaths = currentSession.playerNaturalDeathLog.keySet();
        UUID victimUUID = victim.getUuid();
        if (!recentDeaths.contains(victimUUID)) {
            source.sendError(TextUtils.formatPlain("{} did not die in the last 2 minutes. Or they might have been killed by a player directly.", victim));
            return -1;
        }
        if (player == victim) {
            source.sendError(Text.of("You cannot claim credit for your own death :P"));
            return -1;
        }
        Text textAll = TextUtils.format("{}§7 claims credit for {}§7's death.", player, victim);
        PlayerUtils.broadcastMessageToAdmins(textAll, 200);
        String validateCommand = TextUtils.formatString("/claimkill validate {} {}", player, victim);
        Text adminText = TextUtils.format("§7Click {}§7 to accept the claim if you think it's valid.", TextUtils.runCommandText(validateCommand));
        PlayerUtils.broadcastMessageToAdmins(adminText, 200);

        return 1;
    }

    public int claimCreditAccept(ServerCommandSource source, ServerPlayerEntity killer, ServerPlayerEntity victim) {
        if (checkBanned(source)) return -1;
        if (killer == null) return -1;
        if (victim == null) return -1;

        Text message = TextUtils.format("{}§7's kill claim on {}§7 was accepted.", killer, victim);
        PlayerUtils.broadcastMessage(message);
        currentSeason.onClaimKill(killer, victim);
        currentSession.playerNaturalDeathLog.remove(victim.getUuid());

        return 1;
    }
}
