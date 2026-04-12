package com.example.fakelag.lag;

import com.example.fakelag.FakeLagMod;
import com.example.fakelag.config.ModConfig;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TriggerManager {
    private static TriggerManager instance;
    private ScheduledFuture<?> timerTask;
    private ScheduledFuture<?> scheduleTask;
    private final AtomicLong triggerEndTime = new AtomicLong(0);
    private ModConfig config;
    private boolean triggered = false;

    private TriggerManager() {}

    public static TriggerManager getInstance() {
        if (instance == null) instance = new TriggerManager();
        return instance;
    }

    public void start(ModConfig cfg) {
        this.config = cfg;
        stop();
        if (cfg.triggerTimerSeconds > 0) {
            timerTask = FakeLagMod.scheduler.scheduleAtFixedRate(this::timerTrigger,
                    cfg.triggerTimerSeconds, cfg.triggerTimerSeconds, TimeUnit.SECONDS);
        }
        if (!cfg.triggerSchedule.isEmpty()) {
            scheduleTask = FakeLagMod.scheduler.scheduleAtFixedRate(this::scheduleTrigger,
                    0, 60, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        if (timerTask != null) timerTask.cancel(false);
        if (scheduleTask != null) scheduleTask.cancel(false);
        triggerEndTime.set(0);
        triggered = false;
    }

    private void timerTrigger() {
        if (config != null && config.triggerTimerSeconds > 0) {
            activateTrigger("timer");
        }
    }

    private void scheduleTrigger() {
        if (config == null || config.triggerSchedule.isEmpty()) return;
        LocalTime now = LocalTime.now();
        String[] times = config.triggerSchedule.split(",");
        for (String t : times) {
            try {
                LocalTime triggerTime = LocalTime.parse(t.trim(), DateTimeFormatter.ofPattern("HH:mm"));
                if (Math.abs(now.getMinute() - triggerTime.getMinute()) <= 1 && now.getHour() == triggerTime.getHour()) {
                    activateTrigger("schedule:" + t);
                    break;
                }
            } catch (Exception ignored) {}
        }
    }

    public void activateTrigger(String source) {
        if (config == null) return;
        long duration = config.triggerDurationSeconds * 1000L;
        triggerEndTime.set(System.currentTimeMillis() + duration);
        triggered = true;
        LagLogger.triggerActivated(source);
        FakeLagMod.LOGGER.info("Trigger activated from {} for {} seconds", source, config.triggerDurationSeconds);
        FakeLagMod.scheduler.schedule(() -> {
            if (System.currentTimeMillis() >= triggerEndTime.get()) {
                triggered = false;
                FakeLagMod.LOGGER.info("Trigger deactivated");
            }
        }, duration, TimeUnit.MILLISECONDS);
    }

    public boolean isTriggered() {
        return triggered && System.currentTimeMillis() < triggerEndTime.get();
    }

    public int getTriggerDelay() {
        if (isTriggered() && config != null) {
            return config.baseDelayMs;
        }
        return 0;
    }
}