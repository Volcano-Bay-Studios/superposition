package org.modogthedev.superposition.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaManager;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableClipResult;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.signal.ClientSignalManager;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.SuperpositionConstants;
import oshi.util.tuples.Pair;

import java.util.List;

public class DebugRenderer {
    public static void renderDebug(LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource, MatrixStack matrixStack, Matrix4fc projectionMatrix, Matrix4fc matrix4fc, int renderTick, DeltaTracker deltaTracker, Camera camera) {
        if (!Minecraft.getInstance().getDebugOverlay().showDebugScreen())
            return;
        float partialTicks = deltaTracker.getRealtimeDeltaTicks();
        matrixStack.matrixPush();
        Level level = Minecraft.getInstance().level;
        Vec3 translation = camera.getPosition().scale(-1);
        matrixStack.translate(translation.x, translation.y, translation.z);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        for (Signal signal : ClientSignalManager.clientSignals.get(level).values()) {
            drawPosBox((PoseStack) matrixStack, vertexConsumer, signal.pos.add(0, 1, 0), 0.3f, 0.5f, 0.5f, 0.9f);
        }
        for (Antenna antenna : AntennaManager.getAntennaList(level)) {
            drawPosBox((PoseStack) matrixStack, vertexConsumer, antenna.getRelativeCenter(), 0.5f, 0.5f, 0.9f, 0.5f);
        }
        for (Cable cable : CableManager.getCables(level)) {
            for (Cable.Point point : cable.getPoints()) {
                Vec3 pos = point.getPosition();
                float width = SuperpositionConstants.cableRadius / 2;
                drawPosBox((PoseStack) matrixStack, vertexConsumer, pos, width, 0.9f, 0.5f, 0.5f);
                Pair<Cable.Point, Integer> pointIndexPair = cable.getPlayerHeldPoint(Minecraft.getInstance().player.getUUID());
                if (pointIndexPair != null && pointIndexPair.getA().equals(point)) {
                    drawPosBox((PoseStack) matrixStack, vertexConsumer, pos, width + .1f, 0.5f, 0.9f, 0.5f);
                    drawPosBox((PoseStack) matrixStack, vertexConsumer, pos, width + .2f, 0.5f, 0.9f, 0.5f);
                }
            }
            Cable.Point point = cable.getPoints().get(cable.getPoints().size() - 1);
            Vec3 pos = point.getPosition();
            float width = SuperpositionConstants.cableRadius / 2;
            drawPosBox((PoseStack) matrixStack, vertexConsumer, pos, width + .1f, 0.9f, 0.9f, 0.5f);
        }
        CableClipResult cableClipResult = new CableClipResult(camera.getPosition(), 8, level);
        Pair<Cable, Cable.Point> cablePointPair = cableClipResult.rayCastForClosest(Minecraft.getInstance().player.getEyePosition().add(Minecraft.getInstance().player.getEyePosition().add(Minecraft.getInstance().player.getForward().subtract(Minecraft.getInstance().player.getEyePosition())).scale(5)), .7f);
        if (cablePointPair != null) {
            Vec3 pos = cablePointPair.getB().getPosition();
            float width = SuperpositionConstants.cableRadius / 2 + .1f;
            drawPosBox((PoseStack) matrixStack, vertexConsumer, pos, width, 0.5f, 0.9f, 0.5f);
        }
        matrixStack.matrixPop();
    }

    public static void drawPosBox(PoseStack poseStack, VertexConsumer vertexConsumer, Vec3 pos, float width, float red, float green, float blue) {
        float x1 = (float) pos.x - width;
        float y1 = (float) pos.y - width;
        float z1 = (float) pos.z - width;
        float x2 = (float) pos.x + width;
        float y2 = (float) pos.y + width;
        float z2 = (float) pos.z + width;
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, x1, y1, z1, x2, y2, z2, red, green, blue, 1.0F, red, green, blue);
    }
}
