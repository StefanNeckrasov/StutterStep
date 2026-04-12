package com.example.fakelag.lag;

import com.example.fakelag.FakeLagMod;
import com.example.fakelag.config.ModConfig;

public class LagProgression {
    private static LagProgression instance;
    private Thread updateThread;
    private volatile boolean running = false;
    private volatile int currentDelay = 0;
    private ModConfig config;

    private LagProgression() {}

    public static LagProgression getInstance() {
        if (instance == null) instance = new LagProgression();
        return instance;
    }

    public void start(ModConfig cfg) {
        this.config = cfg;
        if (!cfg.progressionEnabled) {
            stop();
            return;
        }
        if (running) stop();
        running = true;
        updateThread = new Thread(this::updateLoop);
        updateThread.setDaemon(true);
        updateThread.start();
        FakeLagMod.LOGGER.info("Lag progression started");
    }

    public void stop() {
        running = false;
        if (updateThread != null) {
            updateThread.interrupt();
            updateThread = null;
        }
        currentDelay = 0;
        FakeLagMod.LOGGER.info("Lag progression stopped");
    }

    private void updateLoop() {
        long startTime = System.currentTimeMillis();
        long cycleDuration = config.progressionCycleDuration * 1000L;

        while (running) {
            long elapsed = (System.currentTimeMillis() - startTime) % cycleDuration;
            double progress = (double) elapsed / cycleDuration;
            if (!config.progressionLoop && elapsed >= cycleDuration) {
                progress = 1.0;
            }
            int newDelay = computeDelay(progress);
            currentDelay = newDelay;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private int computeDelay(double progress) {
        double min = config.progressionStartDelay;
        double max = config.progressionEndDelay;
        double range = max - min;
        double value;
        switch (config.progressionType) {
            case 0: // linear
                value = min + range * progress;
                break;
            case 1: // sinusoidal
                value = min + range * (0.5 + 0.5 * Math.sin(progress * 2 * Math.PI));
                break;
            case 2: // random
                value = min + range * Math.random();
                break;
            case 3: // step
                int steps = config.progressionSteps;
                double stepSize = 1.0 / steps;
                int step = (int) (progress / stepSize);
                value = min + range * (step * stepSize);
                break;
            default:
                value = min;
        }
        return (int) Math.round(value);
    }

    public int getCurrentDelay() {
        if (!running || config == null || !config.progressionEnabled) return 0;
        return currentDelay;
    }

    public boolean isActive() {
        return running && config != null && config.progressionEnabled;
    }
}