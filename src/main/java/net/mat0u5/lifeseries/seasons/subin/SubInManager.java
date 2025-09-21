package net.mat0u5.lifeseries.seasons.subin;

import com.mojang.authlib.GameProfile;
import net.mat0u5.lifeseries.utils.interfaces.IPlayerManager;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.*;

//? if < 1.21.6
import net.minecraft.nbt.NbtCompound;
//? if >= 1.21.6 {
/*import net.minecraft.storage.ReadView;
import net.minecraft.util.ErrorReporter;
*///?}

public class SubInManager {
    public static List<SubIn> subIns = new ArrayList<>();

    public static void addSubIn(ServerPlayerEntity player, GameProfile targetProfile) {
        UUID playerUUID = player.getUuid();
        GameProfile playerProfile = player.getGameProfile();

        for (SubIn subIn : new ArrayList<>(subIns)) {
            if (subIn.substituter().getId().equals(targetProfile.getId()) || subIn.target().getId().equals(targetProfile.getId()) ||
                    subIn.substituter().getId().equals(playerUUID) || subIn.target().getId().equals(playerUUID)
            ) {
                removeSubIn(subIn);
            }
        }

        savePlayer(player);
        subIns.add(new SubIn(playerProfile, targetProfile));
        loadPlayer(player);

        PlayerUtils.updatePlayerInventory(player);
        player.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(player.experienceProgress, player.totalExperience, player.experienceLevel));

        Integer subInLives = livesManager.getScoreLives(targetProfile.getName());
        if (subInLives == null) {
            livesManager.resetPlayerLife(player);
        }
        else {
            livesManager.setPlayerLives(player, subInLives);
        }
    }

    public static void removeSubIn(ServerPlayerEntity player) {
        UUID playerUUID = player.getUuid();
        for (SubIn subIn : new ArrayList<>(subIns)) {
            if (subIn.substituter().getId().equals(playerUUID) || subIn.target().getId().equals(playerUUID)) {
                removeSubIn(subIn);
            }
        }
    }

    private static void removeSubIn(SubIn subIn) {
        ServerPlayerEntity player1 = PlayerUtils.getPlayer(subIn.substituter().getId());
        ServerPlayerEntity player2 = PlayerUtils.getPlayer(subIn.target().getId());
        if (player1 != null) {
            player1.sendMessage(TextUtils.formatLoosely("§6You are no longer subbing in for {}", subIn.target().getName()));
        }
        if (player2 != null) {
            player2.sendMessage(TextUtils.formatLoosely("§6{} is no longer subbing in for you", subIn.substituter().getName()));
        }

        savePlayer(player1);
        subIns.remove(subIn);
        loadPlayer(player1);
        loadPlayer(player2);
    }

    public static void savePlayer(ServerPlayerEntity player) {
        if (player == null || server == null) return;

        if (server.getPlayerManager() instanceof IPlayerManager iPlayerManager) {
            iPlayerManager.ls$savePlayerData(player);
        }
    }

    public static void loadPlayer(ServerPlayerEntity player) {
        if (player == null || server == null) return;

        if (server.getPlayerManager() instanceof IPlayerManager iPlayerManager) {
            //? if < 1.21.6 {
            Optional<NbtCompound> data = iPlayerManager.ls$getSaveHandler().loadPlayerData(player);
            data.ifPresent(nbt -> {
                player.readNbt(nbt);
                PlayerUtils.teleport(player, player.getPos());
            });
            //?} else {
            /*Optional<ReadView> data = iPlayerManager.ls$getSaveHandler().loadPlayerData(player, ErrorReporter.EMPTY);
            data.ifPresent(nbt -> {
                player.readData(nbt);
                PlayerUtils.teleport(player, player.getPos());
            });
            *///?}
        }
    }

    public static boolean isSubbingIn(UUID uuid) {
        if (uuid == null) return false;
        for (SubIn subIn : subIns) {
            if (subIn.substituter().getId().equals(uuid)) return true;
        }
        return false;
    }

    public static boolean isBeingSubstituted(UUID uuid) {
        if (uuid == null) return false;
        for (SubIn subIn : subIns) {
            if (subIn.target().getId().equals(uuid)) return true;
        }
        return false;
    }

    public static GameProfile getSubstitutedPlayer(UUID uuid) {
        if (uuid == null) return null;
        for (SubIn subIn : subIns) {
            if (subIn.substituter().getId().equals(uuid)) return subIn.target();
        }
        return null;
    }

    public static GameProfile getSubstitutingPlayer(UUID uuid) {
        if (uuid == null) return null;
        for (SubIn subIn : subIns) {
            if (subIn.target().getId().equals(uuid)) return subIn.substituter();
        }
        return null;
    }

    public static UUID getSubstitutedPlayerUUID(UUID uuid) {
        GameProfile profile = getSubstitutedPlayer(uuid);
        if (profile == null) return null;
        return profile.getId();
    }

    public static UUID getSubstitutingPlayerUUID(UUID uuid) {
        GameProfile profile = getSubstitutingPlayer(uuid);
        if (profile == null) return null;
        return profile.getId();
    }

    public record SubIn(GameProfile substituter, GameProfile target) {
    }
}
