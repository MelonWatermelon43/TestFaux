package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.PlayerSaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

//? if >= 1.21.9
/*import net.minecraft.server.PlayerConfigEntry;*/

@Mixin(value = PlayerSaveHandler.class, priority = 1)
public class PlayerSaveHandlerMixin {

    @Redirect(method = "savePlayerData", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getUuidAsString()Ljava/lang/String;"))
    public String subInSave(PlayerEntity instance) {
        return ls$getStringUUIDForPlayer(instance);
    }

    //? if <= 1.21.6 {
    @Redirect(method = "loadPlayerData(Lnet/minecraft/entity/player/PlayerEntity;Ljava/lang/String;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getUuidAsString()Ljava/lang/String;"))
    public String subInLoad(PlayerEntity instance) {
        return ls$getStringUUIDForPlayer(instance);
    }
    //?} else {
    /*@Redirect(method = "loadPlayerData(Lnet/minecraft/server/PlayerConfigEntry;Ljava/lang/String;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerConfigEntry;id()Ljava/util/UUID;"))
    public UUID subInLoad(PlayerConfigEntry instance) {
        return ls$getStringUUIDForPlayer(instance);
    }

    @Unique
    private UUID ls$getStringUUIDForPlayer(PlayerConfigEntry instance) {
        if (Main.isLogicalSide() && !Main.modDisabled() && SubInManager.isSubbingIn(instance.id())) {
            UUID resultUUID = SubInManager.getSubstitutedPlayerUUID(instance.id());
            if (resultUUID != null) {
                return resultUUID;
            }
        }
        return instance.id();
    }
    *///?}

    @Unique
    private String ls$getStringUUIDForPlayer(PlayerEntity instance) {
        if (Main.isLogicalSide() && !Main.modDisabled() && SubInManager.isSubbingIn(instance.getUuid())) {
            UUID resultUUID = SubInManager.getSubstitutedPlayerUUID(instance.getUuid());
            if (resultUUID != null) {
                return resultUUID.toString();
            }
        }
        return instance.getUuidAsString();
    }
}
