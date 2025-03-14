package org.modogthedev.superposition.client.renderer.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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

        for (Float strength : signals) {
            int height = (int) (43 - (strength * 250));
            strength = Math.min(0.084f, strength);
            int offset = (int) (67 - (strength * 800)) + 16;
            int alpha = (int) Mth.clamp(Mth.map(strength, 0.02f, 0.06f, 0, 255), 0, 255);

            int x = (int) ((guiGraphics.guiWidth() / 2f) - offset);
            int x2 = (int) ((guiGraphics.guiWidth() / 2f) + offset);
            int width = Math.max(0, (int) (strength * 50));
            int topWidth = Math.max(0, (int) (strength * 50));
            guiGraphics.fill(RenderType.guiOverlay(), x - width, guiGraphics.guiHeight() / 2 - height, x + width, guiGraphics.guiHeight() / 2 + height, new Color(60, 186, 94, alpha).getRGB());
            guiGraphics.fill(RenderType.guiOverlay(), x2 - width, guiGraphics.guiHeight() / 2 - height, x2 + width, guiGraphics.guiHeight() / 2 + height, new Color(60, 186, 94, alpha).getRGB());

            guiGraphics.fill(RenderType.guiOverlay(), x + width, guiGraphics.guiHeight() / 2 - height, x + (width * 4), guiGraphics.guiHeight() / 2 - height + (topWidth * 2), new Color(60, 186, 94, alpha).getRGB());
            guiGraphics.fill(RenderType.guiOverlay(), x + width, guiGraphics.guiHeight() / 2 + height, x + (width * 4), guiGraphics.guiHeight() / 2 + height - (topWidth * 2), new Color(60, 186, 94, alpha).getRGB());

            guiGraphics.fill(RenderType.guiOverlay(), x2 - (width * 4), guiGraphics.guiHeight() / 2 - height, x2 - width, guiGraphics.guiHeight() / 2 - height + (topWidth * 2), new Color(60, 186, 94, alpha).getRGB());
            guiGraphics.fill(RenderType.guiOverlay(), x2 - (width * 4), guiGraphics.guiHeight() / 2 + height, x2 - width, guiGraphics.guiHeight() / 2 + height - (topWidth * 2), new Color(60, 186, 94, alpha).getRGB());
        }
    }
}
