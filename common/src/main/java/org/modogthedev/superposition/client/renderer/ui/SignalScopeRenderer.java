package org.modogthedev.superposition.client.renderer.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.signal.ClientSignalManager;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.LongRaycast;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SignalScopeRenderer {
    private static final ResourceLocation SPYGLASS_SCOPE_LOCATION = Superposition.id("textures/misc/signal_scope_gui.png");
    public static final ModelResourceLocation SIGNAL_SCOPE_MODEL = ModelResourceLocation.inventory(Superposition.id("signal_scope"));
    public static final ModelResourceLocation SIGNAL_SCOPE_IN_HAND_MODEL = ModelResourceLocation.inventory(
            Superposition.id("signal_scope_hand")
    );

    private static final ResourceLocation SCROLL = Superposition.id("textures/screen/input/scroll_both.png");
    private static final ResourceLocation SHIFT = Superposition.id("textures/screen/input/shift.png");

    public static List<Signal> screenSignals = new ArrayList<>();

    public static float position = 0;
    public static float selectorWidth = 10;

    public static void renderSignalScope(GuiGraphics guiGraphics, float scopeScale) {
        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;

        float f = (float) Math.min(guiGraphics.guiWidth(), guiGraphics.guiHeight());
        float h = Math.min((float) guiGraphics.guiWidth() / f, (float) guiGraphics.guiHeight() / f) * scopeScale;
        int i = Mth.floor(f * h);
        int j = Mth.floor(f * h);
        int k = (guiGraphics.guiWidth() - i) / 2;
        int l = (guiGraphics.guiHeight() - j) / 2;
        int m = k + i;
        int n = l + j;
        RenderSystem.enableBlend();
        guiGraphics.blit(SPYGLASS_SCOPE_LOCATION, k, l, -90, 0.0F, 0.0F, i, j, i, j);
        RenderSystem.disableBlend();
        guiGraphics.fill(RenderType.guiOverlay(), 0, n, guiGraphics.guiWidth(), guiGraphics.guiHeight(), -90, -16777216);
        guiGraphics.fill(RenderType.guiOverlay(), 0, 0, guiGraphics.guiWidth(), l, -90, -16777216);
        guiGraphics.fill(RenderType.guiOverlay(), 0, l, k, n, -90, -16777216);
        guiGraphics.fill(RenderType.guiOverlay(), m, l, guiGraphics.guiWidth(), n, -90, -16777216);

        float screenWidth = guiGraphics.guiWidth();
        float screenHeight = guiGraphics.guiHeight();

        List<Float> signals = new ArrayList<>();

        for (Signal signal : ClientSignalManager.clientSignals.get(level).values()) {
            float dist = (float) Vec3.atLowerCornerOf(player.blockPosition()).distanceTo(Vec3.atLowerCornerOf(SuperpositionMth.blockPosFromVec3(signal.getPos())));
            if (dist < signal.getMaxDist() && dist > signal.getMinDist()) {
                Vec3 vec31 = new Vec3(signal.getPos().x - player.getX(), signal.getPos().y - player.getEyeY(), signal.getPos().z - player.getZ());
                float volume = Mth.map(signal.getAmplitude(), 1, 40, 0.45f, 0.6f);
                volume *= (float) Math.pow(Math.max(0, player.getViewVector(0).normalize().dot(vec31.normalize())), 4) - 0.8f;
                float penetration = LongRaycast.getPenetration(signal.level, signal.getPos(), new Vector3d(player.getX(), player.getY(), player.getZ()));
                volume *= Mth.map(penetration, 0, signal.getFrequency() / 200000, 1, 0);
                volume *= 1.0F / (Math.max(1, dist / (1000000000 / signal.getFrequency())));

                signals.add(volume);
            }
        }

        for (Signal signal : screenSignals) {
            float falloff = Math.min(signal.getFrequency() / 100000 - (position - selectorWidth), (position + selectorWidth) - signal.getFrequency() / 100000);
            float strength = signal.getAmplitude() / 16.7f;

            if (falloff < 0) {
                strength *= -falloff;
            }
            // Render Frequency Finder Bar
            float signalHeight = (float) (4f + (Math.random() * 2) - 1) * (strength * 25);
            float frequencyPosition = Mth.map(signal.getFrequency() / 100000, 0, 160, (screenWidth / 3), (int) ((screenWidth / 3) + (screenWidth / 3)));
            guiGraphics.fill(RenderType.guiOverlay(), (int) (frequencyPosition), (int) ((int) ((screenHeight / 3) + (screenHeight / 2)) - signalHeight), (int) (frequencyPosition) + 2, (int) ((int) ((screenHeight / 3) + (screenHeight / 2)) + signalHeight + 2), new Color(60, 186, 94, 255).getRGB());

            if (falloff < 0) {
                strength /= -falloff;
            }

            strength = Math.min(0.084f, strength);
            int height = (int) (43 - (strength * 250));
            int offset = (int) (67 - (strength * 800)) + 16;
            int alpha = (int) Mth.clamp(Mth.map(strength, 0.02f, 0.06f, 0, 255), 0, 255);

            int x = (int) ((guiGraphics.guiWidth() / 2f) - offset);
            int x2 = (int) ((guiGraphics.guiWidth() / 2f) + offset);
            int width = Math.max(0, (int) (strength * 50));
            int topWidth = Math.max(0, (int) (strength * 50));

            // Render signal locator
            guiGraphics.fill(RenderType.guiOverlay(), x - width, guiGraphics.guiHeight() / 2 - height, x + width, guiGraphics.guiHeight() / 2 + height, new Color(60, 186, 94, alpha).getRGB());
            guiGraphics.fill(RenderType.guiOverlay(), x2 - width, guiGraphics.guiHeight() / 2 - height, x2 + width, guiGraphics.guiHeight() / 2 + height, new Color(60, 186, 94, alpha).getRGB());

            guiGraphics.fill(RenderType.guiOverlay(), x + width, guiGraphics.guiHeight() / 2 - height, x + (width * 4), guiGraphics.guiHeight() / 2 - height + (topWidth * 2), new Color(60, 186, 94, alpha).getRGB());
            guiGraphics.fill(RenderType.guiOverlay(), x + width, guiGraphics.guiHeight() / 2 + height, x + (width * 4), guiGraphics.guiHeight() / 2 + height - (topWidth * 2), new Color(60, 186, 94, alpha).getRGB());

            guiGraphics.fill(RenderType.guiOverlay(), x2 - (width * 4), guiGraphics.guiHeight() / 2 - height, x2 - width, guiGraphics.guiHeight() / 2 - height + (topWidth * 2), new Color(60, 186, 94, alpha).getRGB());
            guiGraphics.fill(RenderType.guiOverlay(), x2 - (width * 4), guiGraphics.guiHeight() / 2 + height, x2 - width, guiGraphics.guiHeight() / 2 + height - (topWidth * 2), new Color(60, 186, 94, alpha).getRGB());
        }
        guiGraphics.fill(RenderType.guiOverlay(), (int) (screenWidth / 3), (int) ((screenHeight / 3) + (screenHeight / 2)), (int) ((screenWidth / 3) + (screenWidth / 3)), (int) ((screenHeight / 3) + (screenHeight / 2)) + 2, new Color(60, 186, 94, 255).getRGB());
        guiGraphics.fill(RenderType.guiOverlay(), (int) (screenWidth / 3), (int) ((screenHeight / 3) + (screenHeight / 2)) - 10, (int) (screenWidth / 3) + 2, (int) ((screenHeight / 3) + (screenHeight / 2)) + 12, new Color(60, 186, 94, 255).getRGB());
        guiGraphics.fill(RenderType.guiOverlay(), (int) ((screenWidth / 3) + (screenWidth / 3)) - 2, (int) ((screenHeight / 3) + (screenHeight / 2)) - 10, (int) ((screenWidth / 3) + (screenWidth / 3)), (int) ((screenHeight / 3) + (screenHeight / 2)) + 12, new Color(60, 186, 94, 255).getRGB());

        position = Mth.clamp(position, 0 + selectorWidth, 160 - selectorWidth);

        float start = Mth.map(position - selectorWidth, 0, 160, (screenWidth / 3), (int) ((screenWidth / 3) + (screenWidth / 3)));
        float end = Math.min(Mth.map(position + selectorWidth, 0, 160, (screenWidth / 3), (int) ((screenWidth / 3) + (screenWidth / 3))), (screenWidth / 3) + (screenWidth / 3) - 2);

        guiGraphics.fill(RenderType.guiOverlay(), (int) (start), (int) ((screenHeight / 3) + (screenHeight / 2)) - 8, (int) (start) + 2, (int) ((screenHeight / 3) + (screenHeight / 2)) + 10, new Color(128, 242, 130, 255).getRGB());
        guiGraphics.fill(RenderType.guiOverlay(), (int) (end), (int) ((screenHeight / 3) + (screenHeight / 2)) - 8, (int) (end) + 2, (int) ((screenHeight / 3) + (screenHeight / 2)) + 10, new Color(128, 242, 130, 255).getRGB());

        guiGraphics.drawCenteredString(Minecraft.getInstance().font, "FREQUENCY: " + SuperpositionMth.formatHz((position - selectorWidth) * 100000) + " - " + SuperpositionMth.formatHz((position + selectorWidth) * 100000), (int) (screenWidth / 2), (int) ((screenHeight / 3) + (screenHeight / 2)) - 35, new Color(78, 208, 114, 255).getRGB());

        //  Render inputs
        guiGraphics.blit(SCROLL, 4, 14, 0, 0, 12, 16, 12, 16);
        guiGraphics.blit(SHIFT, 4, 32, 0, 0, 30, 14, 30, 14);
        guiGraphics.blit(SCROLL, 37, 32, 0, 0, 12, 16, 12, 16);
        guiGraphics.drawString(Minecraft.getInstance().font, "Adjust Frequency", 22, 16, new Color(78, 208, 114, 255).getRGB());
        guiGraphics.drawString(Minecraft.getInstance().font, "Adjust Width", 54, 32, new Color(78, 208, 114, 255).getRGB());
    }

    public static void scroll(float value) {
        if (Screen.hasShiftDown()) {
            selectorWidth = Mth.clamp(selectorWidth + value, 1, 50);
        } else {
            float speed = (value * (Minecraft.getInstance().options.keySprint.isDown() ? 10 : 1));
            position = Mth.clamp(position + speed, 0 + selectorWidth, 160 - selectorWidth);
        }
        position = Mth.clamp(position, 0 + selectorWidth, 160 - selectorWidth);
    }
}
