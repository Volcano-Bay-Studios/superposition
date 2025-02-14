package org.modogthedev.superposition.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.client.render.MatrixStack;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.blockentity.AntennaActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.core.SuperpositionRenderTypes;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableClipResult;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.rope_system.RopeNode;
import org.modogthedev.superposition.util.CatmulRomSpline;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.ArrayList;
import java.util.List;

public class CableRenderer {

    private static final Quaternionf POSITIVE_Y = new Quaternionf().setAngleAxis(Math.PI / 2, 1, 0, 0);
    private static final Quaternionf NEGATIVE_Y = new Quaternionf().setAngleAxis(-Math.PI / 2, 1, 0, 0);

    private static final Quaternionf ORIENTATION = new Quaternionf();
    private static final Quaternionf NEXT_ORIENTATION = new Quaternionf();
    private static final Vector3f POS = new Vector3f();
    private static final Vector3f NORMAL = new Vector3f();
    private static final Vector3f NEXT_NORMAL = new Vector3f();

    private static final List<Vec3> CABLE_POINTS = new ArrayList<>();
    private static final List<Vec3> PREV_CABLE_POINTS = new ArrayList<>();

    // Overstrech drop cable animation
    public static float stretch = 0f;
    public static Vec3 detachPos;
    public static float detachDelta;

    private static final BlockPos.MutableBlockPos LIGHT_POS = new BlockPos.MutableBlockPos();

    public static void renderCables(LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource, MatrixStack matrixStack, Matrix4fc projectionMatrix, Matrix4fc matrix4fc, int renderTick, DeltaTracker deltaTracker, Camera camera) {
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(true);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(SuperpositionRenderTypes.cable());
        ClientLevel level = Minecraft.getInstance().level;
        Vec3 cameraPos = camera.getPosition();
        PoseStack.Pose pose = matrixStack.pose();

        for (Cable cable : CableManager.getLevelCables(level)) {
            float effectiveGeometryPartialTicks = cable.isSleeping() ? 1.0f : partialTicks;
            cable.updateLights(partialTicks);
            CABLE_POINTS.clear();
            PREV_CABLE_POINTS.clear();
            for (RopeNode point : cable.getPoints()) {
                Vec3 pos = point.getPosition();
                CABLE_POINTS.add(pos);
                PREV_CABLE_POINTS.add(point.getPrevRenderPosition());
            }
            List<Vec3> splinePoints = CatmulRomSpline.generateSpline(CABLE_POINTS, SuperpositionConstants.cableSegments);
            List<Vec3> prevSplinePoints = CatmulRomSpline.generateSpline(PREV_CABLE_POINTS, SuperpositionConstants.cableSegments);

            splinePoints.addFirst(cable.getPoints().getFirst().getPosition());
            prevSplinePoints.addFirst(cable.getPoints().getFirst().getPrevRenderPosition());

            splinePoints.add(cable.getPoints().getLast().getPosition());
            prevSplinePoints.add(cable.getPoints().getLast().getPrevRenderPosition());

            int color = 0xFF000000 | cable.getColor().getRGB();
            float cableRadius = SuperpositionConstants.cableWidth / 2.0f;
            float v = 0;
            float nextV;

            renderCableStart(vertexConsumer, matrixStack, cameraPos, color, prevSplinePoints.getFirst(), splinePoints.getFirst(), prevSplinePoints.get(1), splinePoints.get(1), effectiveGeometryPartialTicks);

            for (int i = 0; i < splinePoints.size() - 1; i++) {
                Vec3 prevPoint = prevSplinePoints.get(i);
                Vec3 point = splinePoints.get(i);
                Vec3 prevNextPoint = prevSplinePoints.get(i + 1);
                Vec3 nextPoint = splinePoints.get(i + 1);

                double x = Mth.lerp(effectiveGeometryPartialTicks, prevPoint.x, point.x);
                double y = Mth.lerp(effectiveGeometryPartialTicks, prevPoint.y, point.y);
                double z = Mth.lerp(effectiveGeometryPartialTicks, prevPoint.z, point.z);
                double nextX = Mth.lerp(effectiveGeometryPartialTicks, prevNextPoint.x, nextPoint.x);
                double nextY = Mth.lerp(effectiveGeometryPartialTicks, prevNextPoint.y, nextPoint.y);
                double nextZ = Mth.lerp(effectiveGeometryPartialTicks, prevNextPoint.z, nextPoint.z);

                if (i < splinePoints.size() - 2) {
                    calculateOrientation(NEXT_ORIENTATION, nextX, nextY, nextZ, prevSplinePoints.get(i + 2), splinePoints.get(i + 2), effectiveGeometryPartialTicks);
                } else {
                    NEXT_ORIENTATION.set(ORIENTATION);
                }

                int lightStart = LevelRenderer.getLightColor(level, LIGHT_POS.set(x, y, z));
                int lightEnd = LevelRenderer.getLightColor(level, LIGHT_POS.set(nextX, nextY, nextZ));
                double length = Math.sqrt((nextX - x) * (nextX - x) + (nextY - y) * (nextY - y) + (nextZ - z) * (nextZ - z));
                nextV = v + (float) (length * 16.0 / 6.0);

                // Down
                ORIENTATION.transform(NORMAL.set(0, -1, 0));
                NEXT_ORIENTATION.transform(NEXT_NORMAL.set(0, -1, 0));

                NEXT_ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.5F, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                NEXT_ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.5F, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                // Up
                ORIENTATION.transform(NORMAL.set(0, 1, 0));
                NEXT_ORIENTATION.transform(NEXT_NORMAL.set(0, 1, 0));

                ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                NEXT_ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                NEXT_ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.5F, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.5F, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                // West
                ORIENTATION.transform(NORMAL.set(-1, 0, 0));
                NEXT_ORIENTATION.transform(NEXT_NORMAL.set(-1, 0, 0));

                NEXT_ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.5F, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                NEXT_ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.5F, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                // East
                ORIENTATION.transform(NORMAL.set(1, 0, 0));
                NEXT_ORIENTATION.transform(NEXT_NORMAL.set(1, 0, 0));

                ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.5F, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                NEXT_ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                NEXT_ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.5F, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                ORIENTATION.set(NEXT_ORIENTATION);
                v = nextV;
            }
            renderCableEnd(vertexConsumer, matrixStack, cameraPos, color, prevSplinePoints.getLast(), splinePoints.getLast(), prevSplinePoints.get(prevSplinePoints.size() - 2), splinePoints.get(splinePoints.size() - 2), effectiveGeometryPartialTicks);
        }
        bufferSource.endBatch();
    }

    private static void renderCableStart(VertexConsumer vertexConsumer, MatrixStack matrixStack, Vec3 cameraPos, int color, Vec3 prevPoint, Vec3 point, Vec3 prevNextPoint, Vec3 nextPoint, float partialTicks) {
        PoseStack.Pose pose = matrixStack.pose();
        float cableRadius = SuperpositionConstants.cableWidth / 2.0f;
        double x = Mth.lerp(partialTicks, prevPoint.x, point.x);
        double y = Mth.lerp(partialTicks, prevPoint.y, point.y);
        double z = Mth.lerp(partialTicks, prevPoint.z, point.z);

        // TODO attach to block face
        calculateOrientation(ORIENTATION, x, y, z, prevNextPoint, nextPoint, partialTicks);

        // Draw first face
        int startLight = LevelRenderer.getLightColor(Minecraft.getInstance().level, LIGHT_POS.set(x, y, z));
        ORIENTATION.transform(NORMAL.set(0, 0, -1));
        ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(255,255,255,255)
                .setUv(0.5F, 0.5F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(255,255,255,255)
                .setUv(0.5F, 1.0F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(255,255,255,255)
                .setUv(1.0F, 1.0F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(255,255,255,255)
                .setUv(1.0F, 0.5F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);
    }


    private static void renderCableEnd(VertexConsumer vertexConsumer, MatrixStack matrixStack, Vec3 cameraPos, int color, Vec3 prevPoint, Vec3 point, Vec3 prevNextPoint, Vec3 nextPoint, float partialTicks) {
        PoseStack.Pose pose = matrixStack.pose();
        float cableRadius = SuperpositionConstants.cableWidth / 2.0f;
        double x = Mth.lerp(partialTicks, prevPoint.x, point.x);
        double y = Mth.lerp(partialTicks, prevPoint.y, point.y);
        double z = Mth.lerp(partialTicks, prevPoint.z, point.z);

        // TODO attach to block face
        calculateOrientation(ORIENTATION, x, y, z, prevNextPoint, nextPoint, partialTicks);
        ORIENTATION.rotateAxis((float) Math.PI, 0, 1, 0);

        // Draw first face
        int startLight = LevelRenderer.getLightColor(Minecraft.getInstance().level, LIGHT_POS.set(x, y, z));
        ORIENTATION.transform(NORMAL.set(0, 0, 1));
        ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(255,255,255,255)
                .setUv(0.5F, 0.5F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(255,255,255,255)
                .setUv(0.5F, 1.0F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(255,255,255,255)
                .setUv(1.0F, 1.0F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(255,255,255,255)
                .setUv(1.0F, 0.5F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);
    }

    private static Quaternionf calculateOrientation(Quaternionf store, double x, double y, double z, Vec3 prevNextPoint, Vec3 nextPoint, float partialTicks) {
        double dx = (Mth.lerp(partialTicks, prevNextPoint.x, nextPoint.x) - x);
        double dy = (Mth.lerp(partialTicks, prevNextPoint.y, nextPoint.y) - y);
        double dz = (Mth.lerp(partialTicks, prevNextPoint.z, nextPoint.z) - z);
        float factor = 0;//(float) Mth.smoothstep(1.0-Mth.clamp(8*Math.sqrt(dx * dx + dz * dz), 0.0, 1.0));
        return store.identity()
                .rotateAxis((float) Math.atan2(dx, dz), 0, 1, 0)
                .rotateAxis((float) (Math.acos(dy / Math.sqrt(dx * dx + dy * dy + dz * dz)) - Math.PI / 2.0), 1, 0, 0)
                .slerp(dy < 0 ? POSITIVE_Y : NEGATIVE_Y, factor);
    }

    public static void renderCableHeldPoint(LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource, MatrixStack matrixStack, Matrix4fc projectionMatrix, Matrix4fc matrix4fc, int renderTick, DeltaTracker deltaTracker, Camera camera) {
        if (Minecraft.getInstance().options.hideGui) {
            return;
        }

        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(true);
        PoseStack poseStack = matrixStack.toPoseStack();
        ClientLevel level = Minecraft.getInstance().level;
        Vec3 cameraPos = camera.getPosition();

        float width = 0.12f;
        for (Cable cable : CableManager.getLevelCables(level)) {
            for (Int2IntMap.Entry entry : cable.getPlayerHoldingPointMap().int2IntEntrySet()) {
                if (level.getEntity(entry.getIntKey()) instanceof Player player) {
                    int i = Math.min(cable.getPoints().size() - 1, entry.getIntValue());

                    Vec3 pointPos = cable.getPoints().get(i).getPosition();
                    Vec3 prevPos = cable.getPoints().get(i).getPrevPosition();
                    Vec3 pos = SuperpositionMth.lerpVec3(prevPos, pointPos, partialTicks);
                    if (Minecraft.getInstance().player.equals(player)) {
                        DebugRenderer.renderFilledBox(poseStack, bufferSource, pos.x - cameraPos.x - width, pos.y - cameraPos.y - width, pos.z - cameraPos.z - width, pos.x - cameraPos.x + width, pos.y - cameraPos.y + width, pos.z - cameraPos.z + width, 0.5f + stretch / 2, 0.9f - stretch / 2, 0.5f - stretch / 5, 0f + stretch / 2);
                        width += stretch / 32;
                        DebugRenderer.renderFilledBox(poseStack, bufferSource, pos.x - cameraPos.x - width, pos.y - cameraPos.y - width, pos.z - cameraPos.z - width, pos.x - cameraPos.x + width, pos.y - cameraPos.y + width, pos.z - cameraPos.z + width, 0.5f + stretch / 2, 0.9f - stretch / 2, 0.5f - stretch / 6, 0.5f + stretch / 6);
                    } else {
                        DebugRenderer.renderFilledBox(poseStack, bufferSource, pos.x - cameraPos.x - width, pos.y - cameraPos.y - width, pos.z - cameraPos.z - width, pos.x - cameraPos.x + width, pos.y - cameraPos.y + width, pos.z - cameraPos.z + width, 0.5f, 0.9f, 0.5f, 0.5f);
                    }
                }
            }
        }
        if (detachDelta > 0) {
            float delta = Mth.lerp(partialTicks, detachDelta, detachDelta - 0.2f);
            stretch = 1;
            width = 0.12f - Mth.map(1, 0, 0, 1, delta) * 0.15125f;
            if (width > 0) {
                DebugRenderer.renderFilledBox(poseStack, bufferSource, detachPos.x - cameraPos.x - width, detachPos.y - cameraPos.y - width, detachPos.z - cameraPos.z - width, detachPos.x - cameraPos.x + width, detachPos.y - cameraPos.y + width, detachPos.z - cameraPos.z + width, 1f, 0.4f, 0.3f, 0.8f);
                width += 0.03125f;
                DebugRenderer.renderFilledBox(poseStack, bufferSource, detachPos.x - cameraPos.x - width, detachPos.y - cameraPos.y - width, detachPos.z - cameraPos.z - width, detachPos.x - cameraPos.x + width, detachPos.y - cameraPos.y + width, detachPos.z - cameraPos.z + width, 1f, 0.4f, 0.3f, 0.5f);
            }
        }
        CableClipResult cableClipResult = new CableClipResult(camera.getPosition(), 8, level);
        oshi.util.tuples.Pair<Cable, RopeNode> cablePointPair = cableClipResult.rayCastForClosest(Minecraft.getInstance().player.getEyePosition().add(Minecraft.getInstance().player.getEyePosition().add(Minecraft.getInstance().player.getForward().subtract(Minecraft.getInstance().player.getEyePosition())).scale(5)), .7f);
        if (cablePointPair != null) {
            Vec3 pos = cablePointPair.getB().getPosition(partialTicks);
            if (!cablePointPair.getA().getPlayerHoldingPointMap().containsKey(Minecraft.getInstance().player.getId())) {
                boolean isLast = cablePointPair.getA().getPoints().get(cablePointPair.getA().getPoints().size() - 1).equals(cablePointPair.getB());
                boolean isFirst = cablePointPair.getA().getPoints().get(0).equals(cablePointPair.getB());
                boolean hasAnchor = cablePointPair.getB().getAnchor() != null;

                Vector4f color = getColorForNodeHighlight(isLast, isFirst, hasAnchor);
                if (isLast || isFirst) {
                    width -= 0.03f;
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, pos.x - cameraPos.x - width, pos.y - cameraPos.y - width, pos.z - cameraPos.z - width, pos.x - cameraPos.x + width, pos.y - cameraPos.y + width, pos.z - cameraPos.z + width, color.x, color.y, color.z, color.w);
                    width += 0.03f;
                    color.w = 0.4f;
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, pos.x - cameraPos.x - width, pos.y - cameraPos.y - width, pos.z - cameraPos.z - width, pos.x - cameraPos.x + width, pos.y - cameraPos.y + width, pos.z - cameraPos.z + width, color.x, color.y, color.z, color.w);
                } else {
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, pos.x - cameraPos.x - width, pos.y - cameraPos.y - width, pos.z - cameraPos.z - width, pos.x - cameraPos.x + width, pos.y - cameraPos.y + width, pos.z - cameraPos.z + width, color.x, color.y, color.z, color.w);
                }

                for (RopeNode node : cablePointPair.getA().getPoints()) {
                    if (node == cablePointPair.getB() || node.getAnchor() == null) continue;
                    Vec3 anchorPos = node.getPosition(partialTicks);
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, anchorPos.x - cameraPos.x - width, anchorPos.y - cameraPos.y - width, anchorPos.z - cameraPos.z - width, anchorPos.x - cameraPos.x + width, anchorPos.y - cameraPos.y + width, anchorPos.z - cameraPos.z + width, 0.4f, 0.4f, 0.9f, 0.2f);
                }
            }
        }
    }

    private static Vector4f getColorForNodeHighlight(boolean isLast, boolean isFirst, boolean hasAnchor) {
        if (isLast) {
            return new Vector4f(
                    0.5f, 0.5f, 0.9f,
                    0.6f
            );
        } else if (isFirst) {
            return new Vector4f(
                    0.9f, 0.5f, 0.5f,
                    0.6f
            );
        } else if (hasAnchor) {
            return new Vector4f(
                    0.4f, 0.4f, 0.9f,
                    0.4f
            );
        } else {
            return new Vector4f(
                    0.4f, 0.9f, 0.4f,
                    0.4f
            );
        }
    }

    public static void renderOverlays(LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource, MatrixStack matrixStack, Matrix4fc projectionMatrix, Matrix4fc matrix4fc, int renderTick, DeltaTracker deltaTracker, Camera camera) {
        if (Minecraft.getInstance().options.hideGui) {
            return;
        }
        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        Vec3 cameraPos = camera.getPosition();
        float width = 0.12f;
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult instanceof BlockHitResult blockHitResult) {
            Vec3 pos1 = Vec3.atCenterOf(blockHitResult.getBlockPos());
            if (level.getBlockEntity(BlockPos.containing(pos1)) instanceof AntennaActorBlockEntity antennaActorBlockEntity) {
                matrixStack.matrixPush();
                if (antennaActorBlockEntity.antenna != null) {
                    for (BlockPos pos : antennaActorBlockEntity.antenna.antennaParts) {
                        DebugRenderer.renderFilledBox(matrixStack.toPoseStack(), bufferSource, pos, -0.2f, 0.5f, 0.9f, 0.5f, 0.5f);
                    }
                }
                matrixStack.matrixPop();
            }
            if (level.getBlockEntity(BlockPos.containing(pos1)) instanceof AnalyserBlockEntity analyserBlockEntity && analyserBlockEntity.startDistance != 0) {
                matrixStack.matrixPush();
                BlockPos startPos = analyserBlockEntity.getDistancePosition(analyserBlockEntity.startDistance);
                BlockPos endPos = analyserBlockEntity.getDistancePosition(analyserBlockEntity.endDistance);
                DebugRenderer.renderFilledBox(matrixStack.toPoseStack(), bufferSource, startPos, endPos, 0.5f, 0.9f, 0.5f, 0.5f);
                if (analyserBlockEntity.startDistance > 1) {
                    DebugRenderer.renderFilledBox(matrixStack.toPoseStack(), bufferSource, analyserBlockEntity.getDistancePosition(1), analyserBlockEntity.getDistancePosition(analyserBlockEntity.startDistance - 1), 0.9f, 0.3f, 0.3f, 0.5f);
                }
                BlockPos selectedPos = analyserBlockEntity.getDistancePosition(analyserBlockEntity.distance);
                DebugRenderer.renderFilledBox(matrixStack.toPoseStack(), bufferSource, Math.min(selectedPos.getX(), selectedPos.getX()) - cameraPos.x - width, (double) Math.min(selectedPos.getY(), selectedPos.getY()) - cameraPos.y - width, (double) Math.min(selectedPos.getZ(), selectedPos.getZ()) - cameraPos.z - width, (double) (Math.max(selectedPos.getX(), selectedPos.getX()) + 1) - cameraPos.x + width, (double) (Math.max(selectedPos.getY(), selectedPos.getY()) + 1) - cameraPos.y + width, (double) (Math.max(selectedPos.getZ(), selectedPos.getZ()) + 1) - cameraPos.z + width, 0.3f, 0.3f, 0.9f, 0.5f);
                matrixStack.matrixPop();
            }
        }
    }
}
