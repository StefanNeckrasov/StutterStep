package com.example.fakelag.lag;

import com.example.fakelag.FakeLagMod;
import com.example.fakelag.config.ModConfig;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SpikeManager {
    private static SpikeManager instance;
    private ScheduledFuture<?> spikeTask;
    private final AtomicLong spikeEndTime = new AtomicLong(0);
    private ModConfig config;

    private SpikeManager() {}

    public static SpikeManager getInstance() {
        if (instance == null) instance = new SpikeManager();
        return instance;
    }

    public void start(ModConfig cfg) {
        this.config = cfg;
        stop();
        if (!cfg.spikesEnabled) return;
        spikeTask = FakeLagMod.scheduler.scheduleAtFixedRate(this::triggerSpike,
                cfg.spikeIntervalSeconds, cfg.spikeIntervalSeconds, TimeUnit.SECONDS);
        FakeLagMod.LOGGER.info("Spike manager started");
    }

    public void stop() {
        if (spikeTask != null) {
            spikeTask.cancel(false);
            spikeTask = null;
        }
        spikeEndTime.set(0);
        FakeLagMod.LOGGER.info("Spike manager stopped");
    }

    private void triggerSpike() {
        if (config == null || !config.spikesEnabled) return;
        long now = System.currentTimeMillis();
        long durationMs = config.spikeDurationSeconds * 1000L;
        spikeEndTime.set(now + durationMs);
        LagLogger.spikeOccurred(config.spikeMaxDelay);
        FakeLagMod.LOGGER.debug("Spike triggered for {} ms", durationMs);
    }

    public int getCurrentSpikeDelay() {
        if (!config.spikesEnabled) return 0;
        long now = System.currentTimeMillis();
        if (now < spikeEndTime.get()) {
            int range = config.spikeMaxDelay - config.spikeMinDelay;
            return config.spikeMinDelay + (int)(Math.random() * (range + 1));
        }
        return 0;
    }

    public boolean isSpiking() {
        return System.currentTimeMillis() < spikeEndTime.get();
    }
}