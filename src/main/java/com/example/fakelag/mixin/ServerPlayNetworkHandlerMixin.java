package com.example.fakelag.mixin;

import com.example.fakelag.FakeLagMod;
import com.example.fakelag.config.ConfigManager;
import com.example.fakelag.config.ModConfig;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;


@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow public abstract void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet);
    @Shadow public abstract void onPlayerMove(PlayerMoveC2SPacket packet);
    @Shadow public abstract void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet);
    @Shadow public abstract void onPlayerAction(PlayerActionC2SPacket packet);

    @Inject(method = "onPlayerInteractEntity", at = @At("HEAD"), cancellable = true)
    private void onHandleInteractEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        handleDelay(packet, ci, "attack", () -> onPlayerInteractEntity(packet));
    }

    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    private void onHandlePlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        handleDelay(packet, ci, "move", () -> onPlayerMove(packet));
    }

    @Inject(method = "onPlayerInteractBlock", at = @At("HEAD"), cancellable = true)
    private void onHandleInteractBlock(PlayerInteractBlockC2SPacket packet, CallbackInfo ci) {
        handleDelay(packet, ci, "interact", () -> onPlayerInteractBlock(packet));
    }

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    private void onHandlePlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {
        handleDelay(packet, ci, "break", () -> onPlayerAction(packet));
    }

    private void handleDelay(Packet<?> packet, CallbackInfo ci, String type, Runnable originalHandler) {
        ModConfig config = ConfigManager.getActiveConfig();
        if (!config.enabled || !config.serverSideEnabled) return;

        boolean shouldDelay = false;
        if (config.usePerPacketDelay) {
            switch (type) {
                case "attack" -> shouldDelay = config.delayAttack;
                case "move" -> shouldDelay = config.delayMove;
                case "interact" -> shouldDelay = config.delayInteract;
                case "break" -> shouldDelay = config.delayBreakBlock;
            }
        } else {
            shouldDelay = true;
        }

        if (shouldDelay) {
            int delay = config.getEffectiveDelay();
            if (delay > 0) {
                ci.cancel();
                FakeLagMod.scheduler.schedule(originalHandler, delay, TimeUnit.MILLISECONDS);
            }
        }
    }
}