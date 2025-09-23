package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class WatcherCommand extends Command {

    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() != Seasons.UNASSIGNED;
    }

    @Override
    public Text getBannedText() {
        return Text.of("This command is only available when you have selected a series.");
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("watcher")
                .requires(PermissionManager::isAdmin)
                .executes(context -> info(context.getSource()))
                .then(literal("info")
                    .executes(context -> info(context.getSource()))
                )
                .then(literal("list")
                        .executes(context -> listWatchers(context.getSource()))
                )
                .then(literal("add")
                    .then(argument("player", EntityArgumentType.players())
                        .executes(context -> addWatchers(
                            context.getSource(), EntityArgumentType.getPlayers(context, "player"))
                        )
                    )
                )
                .then(literal("remove")
                    .then(argument("player", EntityArgumentType.players())
                        .executes(context -> removeWatchers(
                            context.getSource(), EntityArgumentType.getPlayers(context, "player"))
                        )
                    )
                )
        );
    }

    public int info(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7Watchers are players that are online, but are not affected by most season mechanics. They can only observe."));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7This is very useful for spectators and for admins."));
        //OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§8§oNOTE: This is an experimental feature, report any bugs you find!"));
        return 1;
    }

    public int listWatchers(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        if (WatcherManager.getWatchers().isEmpty()) {
            source.sendError(Text.of("There are no Watchers right now"));
            return -1;
        }
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.formatLoosely("Current Watchers: §7{}", WatcherManager.getWatchers()));
        return 1;
    }

    public int addWatchers(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        targets.forEach(WatcherManager::addWatcher);
        WatcherManager.reloadWatchers();

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("{} is now a Watcher", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("{} targets are now Watchers", targets.size()));
        }

        return 1;
    }

    public int removeWatchers(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        targets.forEach(WatcherManager::removeWatcher);
        WatcherManager.reloadWatchers();

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("{} is no longer a Watcher", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("{} targets are no longer Watchers", targets.size()));
        }

        return 1;
    }
}
