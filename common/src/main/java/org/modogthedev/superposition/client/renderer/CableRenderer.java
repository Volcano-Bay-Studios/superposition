package org.modogthedev.superposition.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.client.render.MatrixStack;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.modogthedev.superposition.core.SuperpositionRenderTypes;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableClipResult;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.util.CatmulRomSpline;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.SuperpositionConstants;

import java.util.ArrayList;
import java.util.List;

public class CableRenderer {

    private static final Quaternionf ORIENTATION = new Quaternionf();
    private static final Quaternionf NEXT_ORIENTATION = new Quaternionf();
    private static final Vector3f POS = new Vector3f();
    private static final Vector3f NORMAL = new Vector3f();
    private static final Vector3f NEXT_NORMAL = new Vector3f();
    private static final IntList LIGHT_COLORS = new IntArrayList();

    private static final List<Vec3> CABLE_POINTS = new ArrayList<>();
    private static final List<Vec3> PREV_CABLE_POINTS = new ArrayList<>();


    // Overstrech drop cable animation
    public static float stretch = 0f;
    public static Vec3 detachPos;
    public static float detachDelta;

    private static final BlockPos.MutableBlockPos LIGHT_POS = new BlockPos.MutableBlockPos();

    public static void renderCables(LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource, MatrixStack matrixStack, Matrix4fc projectionMatrix, Matrix4fc matrix4fc, int renderTick, DeltaTracker deltaTracker, Camera camera) {
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(SuperpositionRenderTypes.cable());
        ClientLevel level = Minecraft.getInstance().level;
        Vec3 cameraPos = camera.getPosition();
        PoseStack.Pose pose = matrixStack.pose();

        for (Cable cable : CableManager.getLevelCables(level)) {
            CABLE_POINTS.clear();
            PREV_CABLE_POINTS.clear();
            LIGHT_COLORS.clear();
            for (Cable.Point point : cable.getPoints()) {
                Vec3 pos = point.getPosition();
                CABLE_POINTS.add(pos);
                PREV_CABLE_POINTS.add(point.getPrevPosition());
                LIGHT_COLORS.add(LevelRenderer.getLightColor(level, LIGHT_POS.set(pos.x, pos.y, pos.z)));
            }
            List<Vec3> points = CatmulRomSpline.generateSpline(CABLE_POINTS, SuperpositionConstants.cableSegments);
            List<Vec3> prevPoints = CatmulRomSpline.generateSpline(PREV_CABLE_POINTS, SuperpositionConstants.cableSegments);
            points.addFirst(cable.getPoints().getFirst().getPosition());
            prevPoints.addFirst(cable.getPoints().getFirst().getPrevPosition());
            points.add(cable.getPoints().getLast().getPosition());
            prevPoints.add(cable.getPoints().getLast().getPrevPosition());

            int color = 0xFF000000 | cable.getColor().getRGB();
            float cableRadius = SuperpositionConstants.cableWidth / 2.0f;
            float v = 0;
            float nextV;

            renderCableStart(vertexConsumer, matrixStack, cameraPos, color, prevPoints.getFirst(), points.getFirst(), prevPoints.get(1), points.get(1), partialTicks);
            for (int i = 0; i < points.size() - 1; i++) {
                Vec3 prevPoint = prevPoints.get(i);
                Vec3 point = points.get(i);
                Vec3 prevNextPoint = prevPoints.get(i + 1);
                Vec3 nextPoint = points.get(i + 1);

                double x = net.minecraft.util.Mth.lerp(partialTicks, prevPoint.x, point.x);
                double y = net.minecraft.util.Mth.lerp(partialTicks, prevPoint.y, point.y);
                double z = net.minecraft.util.Mth.lerp(partialTicks, prevPoint.z, point.z);
                double nextX = net.minecraft.util.Mth.lerp(partialTicks, prevNextPoint.x, nextPoint.x);
                double nextY = net.minecraft.util.Mth.lerp(partialTicks, prevNextPoint.y, nextPoint.y);
                double nextZ = net.minecraft.util.Mth.lerp(partialTicks, prevNextPoint.z, nextPoint.z);

                if (i < points.size() - 2) {
                    calculateOrientation(NEXT_ORIENTATION, nextX, nextY, nextZ, prevPoints.get(i + 2), points.get(i + 2), partialTicks);
                } else {
                    NEXT_ORIENTATION.set(ORIENTATION);
                }

                int lightStart = LIGHT_COLORS.getInt(net.minecraft.util.Mth.clamp(i / SuperpositionConstants.cableSegments, 0, LIGHT_COLORS.size()));
                int lightEnd = LIGHT_COLORS.getInt(net.minecraft.util.Mth.clamp((i + 1) / SuperpositionConstants.cableSegments, 0, LIGHT_COLORS.size()));
                double length = Math.sqrt((nextX - x) * (nextX - x) + (nextY - y) * (nextY - y) + (nextZ - z) * (nextZ - z));
                nextV = v + (float) (length * 16.0 / 6.0);

                // Down
                ORIENTATION.transform(NORMAL.set(0, -1, 0));
                NEXT_ORIENTATION.transform(NEXT_NORMAL.set(0, -1, 0));

                NEXT_ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                NEXT_ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                // Up
                ORIENTATION.transform(NORMAL.set(0, 1, 0));
                NEXT_ORIENTATION.transform(NEXT_NORMAL.set(0, 1, 0));

                ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                NEXT_ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                NEXT_ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                // West
                ORIENTATION.transform(NORMAL.set(-1, 0, 0));
                NEXT_ORIENTATION.transform(NEXT_NORMAL.set(-1, 0, 0));

                NEXT_ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                NEXT_ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                // East
                ORIENTATION.transform(NORMAL.set(1, 0, 0));
                NEXT_ORIENTATION.transform(NEXT_NORMAL.set(1, 0, 0));

                ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, v)
                        .setLight(lightStart)
                        .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

                NEXT_ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                NEXT_ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
                vertexConsumer.addVertex(pose, (float) (nextX - cameraPos.x + POS.x), (float) (nextY - cameraPos.y + POS.y), (float) (nextZ - cameraPos.z + POS.z))
                        .setColor(color)
                        .setUv(0.25F, nextV)
                        .setLight(lightEnd)
                        .setNormal(pose, NEXT_NORMAL.x, NEXT_NORMAL.y, NEXT_NORMAL.z);

                ORIENTATION.set(NEXT_ORIENTATION);
                v = nextV;
            }
            renderCableEnd(vertexConsumer, matrixStack, cameraPos, color, prevPoints.getLast(), points.getLast(), prevPoints.get(prevPoints.size() - 2), points.get(points.size() - 2), partialTicks);
        }
        bufferSource.endBatch();
    }

    private static void renderCableStart(VertexConsumer vertexConsumer, MatrixStack matrixStack, Vec3 cameraPos, int color, Vec3 prevPoint, Vec3 point, Vec3 prevNextPoint, Vec3 nextPoint, float partialTicks) {
        PoseStack.Pose pose = matrixStack.pose();
        float cableRadius = SuperpositionConstants.cableWidth / 2.0f;
        double x = net.minecraft.util.Mth.lerp(partialTicks, prevPoint.x, point.x);
        double y = net.minecraft.util.Mth.lerp(partialTicks, prevPoint.y, point.y);
        double z = net.minecraft.util.Mth.lerp(partialTicks, prevPoint.z, point.z);

        // TODO attach to block face
        calculateOrientation(ORIENTATION, x, y, z, prevNextPoint, nextPoint, partialTicks);

        // Draw first face
        int startLight = LIGHT_COLORS.getInt(0);
        ORIENTATION.transform(NORMAL.set(0, 0, -1));
        ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(color)
                .setUv(0.5F, 0.5F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(color)
                .setUv(0.5F, 1.0F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(color)
                .setUv(1.0F, 1.0F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(color)
                .setUv(1.0F, 0.5F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);
    }


    private static void renderCableEnd(VertexConsumer vertexConsumer, MatrixStack matrixStack, Vec3 cameraPos, int color, Vec3 prevPoint, Vec3 point, Vec3 prevNextPoint, Vec3 nextPoint, float partialTicks) {
        PoseStack.Pose pose = matrixStack.pose();
        float cableRadius = SuperpositionConstants.cableWidth / 2.0f;
        double x = net.minecraft.util.Mth.lerp(partialTicks, prevPoint.x, point.x);
        double y = net.minecraft.util.Mth.lerp(partialTicks, prevPoint.y, point.y);
        double z = net.minecraft.util.Mth.lerp(partialTicks, prevPoint.z, point.z);

        // TODO attach to block face
        calculateOrientation(ORIENTATION, x, y, z, prevNextPoint, nextPoint, partialTicks);
        ORIENTATION.rotateAxis((float) Math.PI, 0, 1, 0);

        // Draw first face
        int startLight = LIGHT_COLORS.getInt(0);
        ORIENTATION.transform(NORMAL.set(0, 0, 1));
        ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(color)
                .setUv(0.5F, 0.5F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(color)
                .setUv(0.5F, 1.0F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(color)
                .setUv(1.0F, 1.0F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
        vertexConsumer.addVertex(pose, (float) (x - cameraPos.x + POS.x), (float) (y - cameraPos.y + POS.y), (float) (z - cameraPos.z + POS.z))
                .setColor(color)
                .setUv(1.0F, 0.5F)
                .setLight(startLight)
                .setNormal(pose, NORMAL.x, NORMAL.y, NORMAL.z);
    }

    private static Quaternionf calculateOrientation(Quaternionf store, double x, double y, double z, Vec3 prevNextPoint, Vec3 nextPoint, float partialTicks) {
        double dx = (net.minecraft.util.Mth.lerp(partialTicks, prevNextPoint.x, nextPoint.x) - x);
        double dy = (net.minecraft.util.Mth.lerp(partialTicks, prevNextPoint.y, nextPoint.y) - y);
        double dz = (net.minecraft.util.Mth.lerp(partialTicks, prevNextPoint.z, nextPoint.z) - z);
        return store.identity()
                .rotateAxis((float) Math.atan2(dx, dz), 0, 1, 0)
                .rotateAxis((float) (Math.acos(dy / Math.sqrt(dx * dx + dy * dy + dz * dz)) - Math.PI / 2.0), 1, 0, 0);
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
                    int i = entry.getIntValue();

                    Vec3 pointPos = cable.getPoints().get(i).getPosition();
                    Vec3 prevPos = cable.getPoints().get(i).getPrevPosition();
                    Vec3 pos = Mth.lerpVec3(prevPos, pointPos, partialTicks);
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
            float delta = net.minecraft.util.Mth.lerp(partialTicks, detachDelta, detachDelta - 0.2f);
            stretch = 1;
            width = 0.12f - Mth.getFromRange(1, 0, 0, 1, delta) * 0.15125f;
            if (width > 0) {
                DebugRenderer.renderFilledBox(poseStack, bufferSource, detachPos.x - cameraPos.x - width, detachPos.y - cameraPos.y - width, detachPos.z - cameraPos.z - width, detachPos.x - cameraPos.x + width, detachPos.y - cameraPos.y + width, detachPos.z - cameraPos.z + width, 1f, 0.4f, 0.3f, 0.8f);
                width += 0.03125f;
                DebugRenderer.renderFilledBox(poseStack, bufferSource, detachPos.x - cameraPos.x - width, detachPos.y - cameraPos.y - width, detachPos.z - cameraPos.z - width, detachPos.x - cameraPos.x + width, detachPos.y - cameraPos.y + width, detachPos.z - cameraPos.z + width, 1f, 0.4f, 0.3f, 0.5f);
            }
        }
        CableClipResult cableClipResult = new CableClipResult(camera.getPosition(), 8, level);
        oshi.util.tuples.Pair<Cable, Cable.Point> cablePointPair = cableClipResult.rayCastForClosest(Minecraft.getInstance().player.getEyePosition().add(Minecraft.getInstance().player.getEyePosition().add(Minecraft.getInstance().player.getForward().subtract(Minecraft.getInstance().player.getEyePosition())).scale(5)), .7f);
        if (cablePointPair != null) {
            Vec3 pointPos = cablePointPair.getB().getPosition();
            Vec3 prevPos = cablePointPair.getB().getPrevPosition();
            Vec3 pos = Mth.lerpVec3(prevPos, pointPos, partialTicks);
            if (!cablePointPair.getA().getPlayerHoldingPointMap().containsKey(Minecraft.getInstance().player.getId())) {
                if (cablePointPair.getA().getPoints().get(cablePointPair.getA().getPoints().size() - 1).equals(cablePointPair.getB()) || cablePointPair.getA().getPoints().get(0).equals(cablePointPair.getB())) {
                    width -= 0.03f;
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, pos.x - cameraPos.x - width, pos.y - cameraPos.y - width, pos.z - cameraPos.z - width, pos.x - cameraPos.x + width, pos.y - cameraPos.y + width, pos.z - cameraPos.z + width, 0.5f, 0.9f, 0.5f, 0.2f);
                    width += 0.03f;
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, pos.x - cameraPos.x - width, pos.y - cameraPos.y - width, pos.z - cameraPos.z - width, pos.x - cameraPos.x + width, pos.y - cameraPos.y + width, pos.z - cameraPos.z + width, 0.5f, 0.9f, 0.5f, 0.4f);
                } else {
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, pos.x - cameraPos.x - width, pos.y - cameraPos.y - width, pos.z - cameraPos.z - width, pos.x - cameraPos.x + width, pos.y - cameraPos.y + width, pos.z - cameraPos.z + width, 0.5f, 0.9f, 0.5f, 0.4f);
                }
            }
        }
    }
}
