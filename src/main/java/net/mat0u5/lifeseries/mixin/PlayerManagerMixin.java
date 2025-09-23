package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.interfaces.IPlayerManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.PlayerSaveHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;
import java.util.function.Predicate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static net.mat0u5.lifeseries.Main.currentSeason;
//? if <= 1.21.5
import net.minecraft.nbt.NbtCompound;
//? if >= 1.21.6 {
/*import net.minecraft.util.ErrorReporter;
import net.minecraft.storage.ReadView;
*///?}
//? if <= 1.21.6 {
import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import java.util.Optional;
//?}

@Mixin(value = PlayerManager.class, priority = 1)
public abstract class PlayerManagerMixin implements IPlayerManager {

    @Final
    @Shadow
    private PlayerSaveHandler saveHandler;

    @Override
    public PlayerSaveHandler ls$getSaveHandler() {
        return saveHandler;
    }


    @Invoker("savePlayerData")
    abstract void invokeSavePlayerData(ServerPlayerEntity player);

    @Override
    public void ls$savePlayerData(ServerPlayerEntity player) {
        invokeSavePlayerData(player);
    }

    @Inject(method = "broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Z)V", at = @At("HEAD"), cancellable = true)
    public void broadcast(Text message, Function<ServerPlayerEntity, Text> playerMessageFactory, boolean overlay, CallbackInfo ci) {
        if (Main.modFullyDisabled()) return;
        if (message.getString().contains("`")) ci.cancel();
    }

    @Inject(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V", at = @At("HEAD"), cancellable = true)
    public void broadcast(SignedMessage message, Predicate<ServerPlayerEntity> shouldSendFiltered, ServerPlayerEntity sender, MessageType.Parameters params, CallbackInfo ci) {
        if (Main.modFullyDisabled()) return;
        if (message.getContent().getString().contains("`")) ci.cancel();
    }

    //? if <= 1.21.6 {
    @Inject(method = "loadPlayerData", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
            //? if <= 1.21.5 {
    public void loadPlayerData(ServerPlayerEntity player, CallbackInfoReturnable<Optional<NbtCompound>> cir) {
     //?} else {
    /*public void loadPlayerData(ServerPlayerEntity player, ErrorReporter errorReporter, CallbackInfoReturnable<Optional<ReadView>> cir) {
        *///?}
        if (Main.modFullyDisabled()) return;
        if (player instanceof FakePlayer fakePlayer) {
            fakePlayer.fixStartingPosition.run();
        }
    }
    //?}

    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    public void respawnPlayer(ServerPlayerEntity player, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        if (alive || removalReason != Entity.RemovalReason.KILLED) return;
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        currentSeason.onPlayerRespawn(cir.getReturnValue());
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    public void skipLoginMessage(PlayerManager instance, Text message, boolean overlay, ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData) {
        if (!Main.isLogicalSide() || Main.modDisabled() || player == null) {
            instance.broadcast(message, overlay);
        }
        PlayerUtils.broadcastToVisiblePlayers(player, message);
    }
}
