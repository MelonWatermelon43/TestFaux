package net.mat0u5.lifeseries.seasons.season.doublelife;

import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class DoubleLifeBoogeymanManager extends BoogeymanManager {
    public boolean skipNextInfiniteCall = false;

    @Override
    public boolean isBoogeymanThatCanBeCured(ServerPlayerEntity player, ServerPlayerEntity victim) {
        if (currentSeason instanceof DoubleLife doubleLife && DoubleLife.SOULBOUND_BOOGEYMAN) {
            ServerPlayerEntity soulmate = doubleLife.getSoulmate(player);
            if (soulmate != null) {
                if (soulmate == victim) {
                    return false;
                }
            }
        }
        return super.isBoogeymanThatCanBeCured(player, victim);
    }

    @Override
    public void addBoogeymanManually(ServerPlayerEntity player) {
        super.addBoogeymanManually(player);
        if (currentSeason instanceof DoubleLife doubleLife && DoubleLife.SOULBOUND_BOOGEYMAN) {
            ServerPlayerEntity soulmate = doubleLife.getSoulmate(player);
            if (soulmate != null) {
                super.addBoogeymanManually(soulmate);
            }
        }
    }

    @Override
    public void removeBoogeymanManually(ServerPlayerEntity player) {
        super.removeBoogeymanManually(player);
        if (currentSeason instanceof DoubleLife doubleLife && DoubleLife.SOULBOUND_BOOGEYMAN) {
            ServerPlayerEntity soulmate = doubleLife.getSoulmate(player);
            if (soulmate != null) {
                super.removeBoogeymanManually(soulmate);
            }
        }
    }

    @Override
    public void cure(ServerPlayerEntity player) {
        super.cure(player);
        if (currentSeason instanceof DoubleLife doubleLife && DoubleLife.SOULBOUND_BOOGEYMAN) {
            ServerPlayerEntity soulmate = doubleLife.getSoulmate(player);
            if (soulmate != null) {
                super.cure(soulmate);
            }
        }
    }

    @Override
    public void playerFailBoogeymanManually(ServerPlayerEntity player, boolean sendMessage) {
        super.playerFailBoogeymanManually(player, sendMessage);
        if (currentSeason instanceof DoubleLife doubleLife && DoubleLife.SOULBOUND_BOOGEYMAN) {
            ServerPlayerEntity soulmate = doubleLife.getSoulmate(player);
            if (soulmate != null) {
                super.playerFailBoogeymanManually(soulmate, sendMessage);
            }
        }
    }

    @Override
    public void chooseNewBoogeyman() {
        if (!DoubleLife.SOULBOUND_BOOGEYMAN || !skipNextInfiniteCall) {
            super.chooseNewBoogeyman();
            return;
        }

        skipNextInfiniteCall = true;
        TaskScheduler.scheduleTask(1, () -> skipNextInfiniteCall = false);
    }

    @Override
    public boolean playerFailBoogeyman(ServerPlayerEntity player, boolean sendMessage) {
        boolean returnValue = super.playerFailBoogeyman(player, sendMessage);
        if (currentSeason instanceof DoubleLife doubleLife) {
            doubleLife.syncSoulboundLives(player);
        }
        return returnValue;
    }

    @Override
    public void handleBoogeymanLists(List<ServerPlayerEntity> normalPlayers, List<ServerPlayerEntity> boogeyPlayers) {
        if (!DoubleLife.SOULBOUND_BOOGEYMAN || !(currentSeason instanceof DoubleLife doubleLife)) {
            super.handleBoogeymanLists(normalPlayers, boogeyPlayers);
            return;
        }
        List<ServerPlayerEntity> newNormalPlayers = new ArrayList<>();
        List<ServerPlayerEntity> newBoogeyPlayers = new ArrayList<>(boogeyPlayers);
        for (ServerPlayerEntity normalPlayer : normalPlayers) {
            ServerPlayerEntity soulmate = doubleLife.getSoulmate(normalPlayer);
            if (soulmate == null || newBoogeyPlayers.contains(normalPlayer)) {
                newNormalPlayers.add(normalPlayer);
                continue;
            }
            if (newBoogeyPlayers.contains(soulmate)) {
                newBoogeyPlayers.add(normalPlayer);
            }
            else {
                newNormalPlayers.add(normalPlayer);
            }
        }
        super.handleBoogeymanLists(newNormalPlayers, newBoogeyPlayers);
    }
}
