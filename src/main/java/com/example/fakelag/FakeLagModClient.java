package com.example.fakelag;

import com.example.fakelag.config.ConfigManager;
import com.example.fakelag.hud.FakeLagHudRenderer;
import com.example.fakelag.network.FakeLagNetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class FakeLagModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Регистрация приёма конфигурации от сервера
        FakeLagNetworkHandler.registerClientReceiver();

        // Регистрация HUD индикатора
        HudRenderCallback.EVENT.register(new FakeLagHudRenderer());

        // Определяем, используем ли серверную конфигурацию
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isOnServer = client.getCurrentServerEntry() != null;
            ConfigManager.setUseServerConfig(isOnServer);
        });
    }
}