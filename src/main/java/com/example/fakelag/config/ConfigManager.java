package com.example.fakelag.config;

import com.example.fakelag.FakeLagMod;
import com.example.fakelag.lag.LagLogger;
import com.example.fakelag.lag.LagProgression;
import com.example.fakelag.lag.SpikeManager;
import com.example.fakelag.lag.TriggerManager;
import com.example.fakelag.network.SyncConfigPayload;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class ConfigManager {
    private static ModConfig clientConfig;
    private static ModConfig serverConfig;
    private static boolean useServerConfig = false;

    static {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        clientConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        serverConfig = new ModConfig();
    }

    public static void loadServerConfig() {
        serverConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        initManagers(serverConfig);
        FakeLagMod.LOGGER.info("Server config loaded: enabled={}, delay={}ms", serverConfig.enabled, serverConfig.baseDelayMs);
    }

    public static void saveServerConfig() {
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }

    public static ModConfig getActiveConfig() {
        if (useServerConfig && serverConfig != null) return serverConfig;
        return clientConfig;
    }

    @Environment(EnvType.CLIENT)
    public static void applyServerConfig(ModConfig config) {
        serverConfig = config;
        useServerConfig = true;
        initManagers(serverConfig);
        FakeLagMod.LOGGER.info("Applied server config: enabled={}, delay={}ms", config.enabled, config.baseDelayMs);
    }

    @Environment(EnvType.CLIENT)
    public static void setUseServerConfig(boolean use) {
        useServerConfig = use;
        if (!use) {
            clientConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            initManagers(clientConfig);
        }
    }

    public static void sendConfigToPlayer(ServerPlayerEntity player) {
        ModConfig cfg = getActiveConfig();
        ServerPlayNetworking.send(player, new SyncConfigPayload(cfg));
    }

    public static void updateServerConfig(ModConfig newConfig) {
        serverConfig = newConfig;
        AutoConfig.getConfigHolder(ModConfig.class).setConfig(newConfig);
        AutoConfig.getConfigHolder(ModConfig.class).save();
        initManagers(serverConfig);
        if (FakeLagMod.serverInstance != null) {
            for (ServerPlayerEntity player : FakeLagMod.serverInstance.getPlayerManager().getPlayerList()) {
                sendConfigToPlayer(player);
            }
        }
    }

    private static void initManagers(ModConfig config) {
        LagProgression.getInstance().start(config);
        SpikeManager.getInstance().start(config);
        TriggerManager.getInstance().start(config);
        LagLogger.init(config.enableLogging);
    }
}