package net.mat0u5.lifeseries.utils.interfaces;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PlayerSaveHandler;

public interface IPlayerManager {
    PlayerSaveHandler ls$getSaveHandler();
    void ls$savePlayerData(ServerPlayerEntity player);
}
