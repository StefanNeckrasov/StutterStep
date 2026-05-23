package com.example.fakelag.hud;

import com.example.fakelag.config.ConfigManager;
import com.example.fakelag.lag.SpikeManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class FakeLagHudRenderer implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden) return;

        var config = ConfigManager.getActiveConfig();
        if (!config.enabled) return;

        TextRenderer textRenderer = client.textRenderer;
        String status = "§cStutterStep: ON§r";
        if (config.clientSideEnabled) status += " §eC§r";
        if (config.serverSideEnabled) status += " §6S§r";
        if (SpikeManager.getInstance().isSpiking()) status += " §c⚡SPIKE§r";
        if (config.packetLossPercent > 0) status += " §7📉" + config.packetLossPercent + "%§r";
        if (config.progressionEnabled) {
            int current = config.getEffectiveDelay();
            status += " §b[PROG: " + current + "ms]§r";
        } else {
            status += " §7" + config.baseDelayMs + "ms§r";
        }

        drawContext.drawText(textRenderer, Text.literal(status), 5, 5, 0xFFFFFF, true);
    }
}