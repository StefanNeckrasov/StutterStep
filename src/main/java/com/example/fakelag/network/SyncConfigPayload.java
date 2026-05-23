package com.example.fakelag.network;

import com.example.fakelag.FakeLagMod;
import com.example.fakelag.config.ModConfig;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncConfigPayload(ModConfig config) implements CustomPayload {
    public static final CustomPayload.Id<SyncConfigPayload> ID = new CustomPayload.Id<>(Identifier.of(FakeLagMod.MOD_ID, "sync_config"));
    public static final PacketCodec<RegistryByteBuf, SyncConfigPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeBoolean(value.config.enabled);
                buf.writeInt(value.config.baseDelayMs);
                buf.writeInt(value.config.jitterMs);
                buf.writeBoolean(value.config.clientSideEnabled);
                buf.writeBoolean(value.config.serverSideEnabled);
                buf.writeBoolean(value.config.delayAttack);
                buf.writeBoolean(value.config.delayMove);
                buf.writeBoolean(value.config.delayInteract);
                buf.writeBoolean(value.config.delayBreakBlock);
                buf.writeBoolean(value.config.usePerPacketDelay);
            },
            buf -> {
                ModConfig cfg = new ModConfig();
                cfg.enabled = buf.readBoolean();
                cfg.baseDelayMs = buf.readInt();
                cfg.jitterMs = buf.readInt();
                cfg.clientSideEnabled = buf.readBoolean();
                cfg.serverSideEnabled = buf.readBoolean();
                cfg.delayAttack = buf.readBoolean();
                cfg.delayMove = buf.readBoolean();
                cfg.delayInteract = buf.readBoolean();
                cfg.delayBreakBlock = buf.readBoolean();
                cfg.usePerPacketDelay = buf.readBoolean();
                return new SyncConfigPayload(cfg);
            }
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}