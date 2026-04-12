package com.example.fakelag.lag;

import com.example.fakelag.FakeLagMod;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LagLogger {
    private static final Path LOG_FILE = Paths.get("logs/StutterStep.log");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static BufferedWriter writer;
    private static boolean enabled = false;

    public static void init(boolean enable) {
        enabled = enable;
        if (enabled) {
            try {
                if (!Files.exists(LOG_FILE.getParent())) {
                    Files.createDirectories(LOG_FILE.getParent());
                }
                writer = Files.newBufferedWriter(LOG_FILE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                log("=== FakeLag logging started ===");
            } catch (IOException e) {
                FakeLagMod.LOGGER.error("Failed to open log file", e);
            }
        } else {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {}
                writer = null;
            }
        }
    }

    public static void log(String message) {
        if (!enabled || writer == null) return;
        try {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            writer.write(timestamp + " " + message + "\n");
            writer.flush();
        } catch (IOException e) {
            FakeLagMod.LOGGER.error("Failed to write log", e);
        }
    }

    public static void packetDelayed(String packetType, int delayMs) {
        log("Packet delayed: " + packetType + " delay=" + delayMs + "ms");
    }

    public static void packetLost(String packetType) {
        log("Packet lost: " + packetType);
    }

    public static void spikeOccurred(int spikeDelay) {
        log("Spike occurred: " + spikeDelay + "ms");
    }

    public static void triggerActivated(String triggerType) {
        log("Trigger activated: " + triggerType);
    }

    public static void configChanged() {
        log("Configuration changed");
    }
}