package com.example.fakelag.config;

import com.example.fakelag.lag.LagProgression;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "fakelag")
public class ModConfig implements ConfigData {
    // Основные настройки
    public boolean enabled = false;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 3000)
    public int baseDelayMs = 100;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1000)
    public int jitterMs = 50;
    public boolean clientSideEnabled = true;
    public boolean serverSideEnabled = true;
    public boolean delayAttack = true;
    public boolean delayMove = true;
    public boolean delayInteract = true;
    public boolean delayBreakBlock = true;
    public boolean usePerPacketDelay = true;

    // Прогрессия
    public boolean progressionEnabled = false;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 3000)
    public int progressionStartDelay = 50;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 3000)
    public int progressionEndDelay = 500;
    @ConfigEntry.BoundedDiscrete(min = 1, max = 300)
    public int progressionCycleDuration = 30;
    public boolean progressionLoop = true;
    @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
    public int progressionSteps = 5;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 3)
    public int progressionType = 0;

    // Потеря пакетов
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int packetLossPercent = 0;
    public boolean lossAttack = true;
    public boolean lossMove = true;
    public boolean lossInteract = true;
    public boolean lossBreakBlock = true;
    public boolean usePerPacketLoss = true;

    // Спайки
    public boolean spikesEnabled = false;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 3000)
    public int spikeMinDelay = 200;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 3000)
    public int spikeMaxDelay = 1000;
    @ConfigEntry.BoundedDiscrete(min = 1, max = 600)
    public int spikeIntervalSeconds = 10;
    @ConfigEntry.BoundedDiscrete(min = 1, max = 30)
    public int spikeDurationSeconds = 2;

    // Триггеры
    public boolean triggerOnDamage = false;
    public boolean triggerOnAttack = false;
    public boolean triggerOnBiomeChange = false;
    public boolean triggerOnDimensionChange = false;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 3600)
    public int triggerTimerSeconds = 0;
    @ConfigEntry.BoundedDiscrete(min = 1, max = 300)
    public int triggerDurationSeconds = 30;
    public String triggerSchedule = "";

    // Логирование
    public boolean enableLogging = false;

    public int getEffectiveDelay() {
        if (!enabled) return 0;
        if (progressionEnabled) {
            int progDelay = LagProgression.getInstance().getCurrentDelay();
            if (progDelay > 0) return progDelay;
        }
        int jitterHalf = jitterMs / 2;
        int jitterDelta = (int) (Math.random() * (jitterMs + 1)) - jitterHalf;
        return Math.max(0, baseDelayMs + jitterDelta);
    }

    public boolean shouldDropPacket(String packetType) {
        if (packetLossPercent <= 0) return false;
        if (Math.random() * 100 >= packetLossPercent) return false;
        if (usePerPacketLoss) {
            switch (packetType) {
                case "attack": return lossAttack;
                case "move": return lossMove;
                case "interact": return lossInteract;
                case "break": return lossBreakBlock;
                default: return true;
            }
        }
        return true;
    }
}