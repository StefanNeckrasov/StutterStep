package com.example.fakelag.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.fakelag.FakeLagMod;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProfileManager {
    private static final Path PROFILES_DIR = Paths.get("config/StutterStep/profiles");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void saveProfile(String name, ModConfig config) {
        try {
            if (!Files.exists(PROFILES_DIR)) Files.createDirectories(PROFILES_DIR);
            Path file = PROFILES_DIR.resolve(name + ".json");
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            FakeLagMod.LOGGER.error("Failed to save profile " + name, e);
        }
    }

    public static ModConfig loadProfile(String name) {
        try {
            Path file = PROFILES_DIR.resolve(name + ".json");
            if (!Files.exists(file)) return null;
            try (Reader reader = Files.newBufferedReader(file)) {
                return GSON.fromJson(reader, ModConfig.class);
            }
        } catch (IOException e) {
            FakeLagMod.LOGGER.error("Failed to load profile " + name, e);
            return null;
        }
    }

    public static String[] listProfiles() {
        try {
            if (!Files.exists(PROFILES_DIR)) return new String[0];
            return Files.list(PROFILES_DIR)
                    .map(p -> p.getFileName().toString().replace(".json", ""))
                    .toArray(String[]::new);
        } catch (IOException e) {
            return new String[0];
        }
    }
}