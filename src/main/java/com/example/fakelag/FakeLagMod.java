package com.example.fakelag;

import com.example.fakelag.command.FakeLagCommand;
import com.example.fakelag.config.ConfigManager;
import com.example.fakelag.network.SyncConfigPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FakeLagMod implements ModInitializer {
    public static final String MOD_ID = "fakelag";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftServer serverInstance = null;
    public static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing StutterStep");

        PayloadTypeRegistry.playS2C().register(SyncConfigPayload.ID, SyncConfigPayload.CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ConfigManager.sendConfigToPlayer(handler.getPlayer());
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                FakeLagCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            serverInstance = server;
            ConfigManager.loadServerConfig();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ConfigManager.saveServerConfig();
            serverInstance = null;
            scheduler.shutdownNow();
        });
    }
}