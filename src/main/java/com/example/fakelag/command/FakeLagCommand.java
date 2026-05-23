package com.example.fakelag.command;

import com.example.fakelag.config.ConfigManager;
import com.example.fakelag.config.ModConfig;
import com.example.fakelag.config.ProfileManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;

public class FakeLagCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("stutterstep")
                .requires(source -> source.hasPermissionLevel(2))

                // Вкл/Выкл
                .then(literal("on").executes(ctx -> {
                    ModConfig cfg = ConfigManager.getActiveConfig();
                    cfg.enabled = true;
                    ConfigManager.updateServerConfig(cfg);
                    ctx.getSource().sendFeedback(() -> Text.literal("StutterStep enabled"), true);
                    return 1;
                }))
                .then(literal("off").executes(ctx -> {
                    ModConfig cfg = ConfigManager.getActiveConfig();
                    cfg.enabled = false;
                    ConfigManager.updateServerConfig(cfg);
                    ctx.getSource().sendFeedback(() -> Text.literal("FakeLag disabled"), true);
                    return 1;
                }))

                // Базовые настройки
                .then(literal("set")
                        .then(literal("delay")
                                .then(argument("ms", IntegerArgumentType.integer(0, 3000))
                                        .executes(ctx -> {
                                            int delay = IntegerArgumentType.getInteger(ctx, "ms");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            cfg.baseDelayMs = delay;
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Base delay set to " + delay + " ms"), true);
                                            return 1;
                                        })))
                        .then(literal("jitter")
                                .then(argument("ms", IntegerArgumentType.integer(0, 1000))
                                        .executes(ctx -> {
                                            int jitter = IntegerArgumentType.getInteger(ctx, "ms");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            cfg.jitterMs = jitter;
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Jitter set to " + jitter + " ms"), true);
                                            return 1;
                                        })))
                        .then(literal("client")
                                .then(argument("enabled", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            cfg.clientSideEnabled = enabled;
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Client-side lag " + (enabled ? "enabled" : "disabled")), true);
                                            return 1;
                                        })))
                        .then(literal("server")
                                .then(argument("enabled", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            cfg.serverSideEnabled = enabled;
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Server-side lag " + (enabled ? "enabled" : "disabled")), true);
                                            return 1;
                                        })))
                        .then(literal("attack")
                                .then(argument("enabled", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            cfg.delayAttack = enabled;
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Attack delay " + (enabled ? "enabled" : "disabled")), true);
                                            return 1;
                                        })))
                        .then(literal("move")
                                .then(argument("enabled", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            cfg.delayMove = enabled;
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Move delay " + (enabled ? "enabled" : "disabled")), true);
                                            return 1;
                                        })))
                        .then(literal("interact")
                                .then(argument("enabled", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            cfg.delayInteract = enabled;
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Interact delay " + (enabled ? "enabled" : "disabled")), true);
                                            return 1;
                                        })))
                        .then(literal("break")
                                .then(argument("enabled", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            cfg.delayBreakBlock = enabled;
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Block break delay " + (enabled ? "enabled" : "disabled")), true);
                                            return 1;
                                        })))
                )

                // Прогрессия
                .then(literal("progression")
                        .then(literal("on").executes(ctx -> {
                            ModConfig cfg = ConfigManager.getActiveConfig();
                            cfg.progressionEnabled = true;
                            ConfigManager.updateServerConfig(cfg);
                            ctx.getSource().sendFeedback(() -> Text.literal("Lag progression enabled"), true);
                            return 1;
                        }))
                        .then(literal("off").executes(ctx -> {
                            ModConfig cfg = ConfigManager.getActiveConfig();
                            cfg.progressionEnabled = false;
                            ConfigManager.updateServerConfig(cfg);
                            ctx.getSource().sendFeedback(() -> Text.literal("Lag progression disabled"), true);
                            return 1;
                        }))
                        .then(literal("set")
                                .then(literal("start").then(argument("ms", IntegerArgumentType.integer(0, 3000))
                                        .executes(ctx -> {
                                            int val = IntegerArgumentType.getInteger(ctx, "ms");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            cfg.progressionStartDelay = val;
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Progression start delay set to " + val + " ms"), true);
                                            return 1;
                                        })))
                                .then(literal("end").then(argument("ms", IntegerArgumentType.integer(0, 3000))
                                        .executes(ctx -> {
                                            int val = IntegerArgumentType.getInteger(ctx, "ms");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            cfg.progressionEndDelay = val;
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Progression end delay set to " + val + " ms"), true);
                                            return 1;
                                        })))
                                .then(literal("duration").then(argument("seconds", IntegerArgumentType.integer(1, 300))
                                        .executes(ctx -> {
                                            int val = IntegerArgumentType.getInteger(ctx, "seconds");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            cfg.progressionCycleDuration = val;
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Cycle duration set to " + val + " seconds"), true);
                                            return 1;
                                        })))
                                .then(literal("type").then(argument("type", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String type = StringArgumentType.getString(ctx, "type");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            switch (type) {
                                                case "linear" -> cfg.progressionType = 0;
                                                case "sinusoidal" -> cfg.progressionType = 1;
                                                case "random" -> cfg.progressionType = 2;
                                                case "step" -> cfg.progressionType = 3;
                                                default -> {
                                                    ctx.getSource().sendError(Text.literal("Unknown type. Use: linear, sinusoidal, random, step"));
                                                    return 0;
                                                }
                                            }
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Progression type set to " + type), true);
                                            return 1;
                                        })))
                                .then(literal("loop").then(argument("loop", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            boolean loop = BoolArgumentType.getBool(ctx, "loop");
                                            ModConfig cfg = ConfigManager.getActiveConfig();
                                            cfg.progressionLoop = loop;
                                            ConfigManager.updateServerConfig(cfg);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Progression loop set to " + loop), true);
                                            return 1;
                                        })))
                        )
                )

                // Потеря пакетов
                .then(literal("loss")
                        .then(literal("set").then(argument("percent", IntegerArgumentType.integer(0, 100))
                                .executes(ctx -> {
                                    int percent = IntegerArgumentType.getInteger(ctx, "percent");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.packetLossPercent = percent;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Packet loss set to " + percent + "%"), true);
                                    return 1;
                                })))
                        .then(literal("attack").then(argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.lossAttack = enabled;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Attack packet loss " + (enabled ? "enabled" : "disabled")), true);
                                    return 1;
                                })))
                        .then(literal("move").then(argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.lossMove = enabled;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Move packet loss " + (enabled ? "enabled" : "disabled")), true);
                                    return 1;
                                })))
                        .then(literal("interact").then(argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.lossInteract = enabled;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Interact packet loss " + (enabled ? "enabled" : "disabled")), true);
                                    return 1;
                                })))
                        .then(literal("break").then(argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.lossBreakBlock = enabled;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Break packet loss " + (enabled ? "enabled" : "disabled")), true);
                                    return 1;
                                })))
                )

                // Спайки
                .then(literal("spike")
                        .then(literal("on").executes(ctx -> {
                            ModConfig cfg = ConfigManager.getActiveConfig();
                            cfg.spikesEnabled = true;
                            ConfigManager.updateServerConfig(cfg);
                            ctx.getSource().sendFeedback(() -> Text.literal("Spikes enabled"), true);
                            return 1;
                        }))
                        .then(literal("off").executes(ctx -> {
                            ModConfig cfg = ConfigManager.getActiveConfig();
                            cfg.spikesEnabled = false;
                            ConfigManager.updateServerConfig(cfg);
                            ctx.getSource().sendFeedback(() -> Text.literal("Spikes disabled"), true);
                            return 1;
                        }))
                        .then(literal("interval").then(argument("seconds", IntegerArgumentType.integer(1, 600))
                                .executes(ctx -> {
                                    int sec = IntegerArgumentType.getInteger(ctx, "seconds");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.spikeIntervalSeconds = sec;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Spike interval set to " + sec + " seconds"), true);
                                    return 1;
                                })))
                        .then(literal("duration").then(argument("seconds", IntegerArgumentType.integer(1, 30))
                                .executes(ctx -> {
                                    int sec = IntegerArgumentType.getInteger(ctx, "seconds");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.spikeDurationSeconds = sec;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Spike duration set to " + sec + " seconds"), true);
                                    return 1;
                                })))
                        .then(literal("min").then(argument("ms", IntegerArgumentType.integer(0, 3000))
                                .executes(ctx -> {
                                    int ms = IntegerArgumentType.getInteger(ctx, "ms");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.spikeMinDelay = ms;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Spike min delay set to " + ms + " ms"), true);
                                    return 1;
                                })))
                        .then(literal("max").then(argument("ms", IntegerArgumentType.integer(0, 3000))
                                .executes(ctx -> {
                                    int ms = IntegerArgumentType.getInteger(ctx, "ms");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.spikeMaxDelay = ms;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Spike max delay set to " + ms + " ms"), true);
                                    return 1;
                                })))
                )

                // Триггеры (упрощённо: таймер и расписание)
                .then(literal("trigger")
                        .then(literal("timer").then(argument("seconds", IntegerArgumentType.integer(0, 3600))
                                .executes(ctx -> {
                                    int sec = IntegerArgumentType.getInteger(ctx, "seconds");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.triggerTimerSeconds = sec;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Trigger timer set to " + sec + " seconds"), true);
                                    return 1;
                                })))
                        .then(literal("duration").then(argument("seconds", IntegerArgumentType.integer(1, 300))
                                .executes(ctx -> {
                                    int sec = IntegerArgumentType.getInteger(ctx, "seconds");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.triggerDurationSeconds = sec;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Trigger duration set to " + sec + " seconds"), true);
                                    return 1;
                                })))
                        .then(literal("schedule").then(argument("times", StringArgumentType.string())
                                .executes(ctx -> {
                                    String times = StringArgumentType.getString(ctx, "times");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.triggerSchedule = times;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Schedule set to " + times), true);
                                    return 1;
                                })))
                        .then(literal("damage").then(argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                    ModConfig cfg = ConfigManager.getActiveConfig();
                                    cfg.triggerOnDamage = enabled;
                                    ConfigManager.updateServerConfig(cfg);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Trigger on damage " + (enabled ? "enabled" : "disabled")), true);
                                    return 1;
                                })))
                )

                // Профили
                .then(literal("profile")
                        .then(literal("save").then(argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    ProfileManager.saveProfile(name, ConfigManager.getActiveConfig());
                                    ctx.getSource().sendFeedback(() -> Text.literal("Profile saved as " + name), true);
                                    return 1;
                                })))
                        .then(literal("load").then(argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    ModConfig profile = ProfileManager.loadProfile(name);
                                    if (profile != null) {
                                        ConfigManager.updateServerConfig(profile);
                                        ctx.getSource().sendFeedback(() -> Text.literal("Profile " + name + " loaded"), true);
                                    } else {
                                        ctx.getSource().sendError(Text.literal("Profile not found"));
                                    }
                                    return 1;
                                })))
                        .then(literal("list").executes(ctx -> {
                            String[] list = ProfileManager.listProfiles();
                            ctx.getSource().sendFeedback(() -> Text.literal("Profiles: " + String.join(", ", list)), false);
                            return 1;
                        }))
                )

                // Логирование
                .then(literal("logging")
                        .then(literal("on").executes(ctx -> {
                            ModConfig cfg = ConfigManager.getActiveConfig();
                            cfg.enableLogging = true;
                            ConfigManager.updateServerConfig(cfg);
                            ctx.getSource().sendFeedback(() -> Text.literal("Logging enabled"), true);
                            return 1;
                        }))
                        .then(literal("off").executes(ctx -> {
                            ModConfig cfg = ConfigManager.getActiveConfig();
                            cfg.enableLogging = false;
                            ConfigManager.updateServerConfig(cfg);
                            ctx.getSource().sendFeedback(() -> Text.literal("Logging disabled"), true);
                            return 1;
                        }))
                )
        );
    }
}