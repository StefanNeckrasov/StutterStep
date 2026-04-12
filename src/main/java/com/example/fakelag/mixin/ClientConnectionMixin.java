package com.example.fakelag.mixin;

import com.example.fakelag.FakeLagMod;
import com.example.fakelag.config.ConfigManager;
import com.example.fakelag.config.ModConfig;
import com.example.fakelag.lag.LagLogger;
import com.example.fakelag.lag.SpikeManager;
import com.example.fakelag.lag.TriggerManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        ModConfig config = ConfigManager.getActiveConfig();
        if (!config.enabled || !config.clientSideEnabled) return;

        // Определение типа пакета
        String packetType = "";
        if (packet instanceof PlayerInteractEntityC2SPacket) packetType = "attack";
        else if (packet instanceof PlayerMoveC2SPacket) packetType = "move";
        else if (packet instanceof PlayerInteractBlockC2SPacket || packet instanceof PlayerInteractItemC2SPacket) packetType = "interact";
        else if (packet instanceof PlayerActionC2SPacket) packetType = "break";

        // Потеря пакетов
        if (config.shouldDropPacket(packetType)) {
            ci.cancel();
            LagLogger.packetLost(packetType);
            return;
        }

        // Выборочная задержка
        boolean shouldDelay = false;
        if (config.usePerPacketDelay) {
            switch (packetType) {
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
            // Спайки
            int spike = SpikeManager.getInstance().getCurrentSpikeDelay();
            if (spike > 0) delay = Math.max(delay, spike);
            // Триггеры
            int trigger = TriggerManager.getInstance().getTriggerDelay();
            if (trigger > 0) delay = Math.max(delay, trigger);

            if (delay > 0) {
                ci.cancel();
                ClientConnection connection = (ClientConnection) (Object) this;
                final int finalDelay = delay;
                final String finalPacketType = packetType;
                FakeLagMod.scheduler.schedule(() -> {
                    connection.send(packet);
                    LagLogger.packetDelayed(finalPacketType, finalDelay);
                }, delay, TimeUnit.MILLISECONDS);
            }
        }
    }
}