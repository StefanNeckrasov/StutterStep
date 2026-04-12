package com.example.fakelag.network;

import com.example.fakelag.config.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class FakeLagNetworkHandler {
    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SyncConfigPayload.ID, (payload, context) -> {
            context.client().execute(() -> ConfigManager.applyServerConfig(payload.config()));
        });
    }
}