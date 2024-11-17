package org.modogthedev.superposition.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableClipResult;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.util.CatenaryArc;
import org.modogthedev.superposition.util.CatmulRomSpline;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.SuperpositionConstants;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Stream;

import static java.lang.Math.round;

public class CableRenderer {
    private static VertexConsumer vertexConsumer;
    private static ResourceLocation CABLE = Superposition.id("textures/screen/cable.png");
    public static float stretch = 0f;
    public static Vec3 detachPos;
    public static float detachDelta;

    public static void renderCables(LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, float partialTicks, Camera camera, Frustum frustum) {
        poseStack.pushPose();
        vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(CABLE));
        ClientLevel level = Minecraft.getInstance().level;
        Matrix4f matrix4f = poseStack.last().pose();
        Vec3 translation = camera.getPosition().scale(-1);
        poseStack.translate(translation.x, translation.y, translation.z);
        for (Cable cable : CableManager.getLevelCables(level)) {
            poseStack.pushPose();
//            poseStack.translate(origin.x,origin.y,origin.z);
            int size = cable.getPoints().size() - 1;
            List<Vec3> cablePoints = new ArrayList<>();
            List<Vec3> prevCablePoints = new ArrayList<>();
            cablePoints.add(cable.getPoints().get(0).getPosition());
            prevCablePoints.add(cable.getPoints().get(0).getPrevPosition());
            for (Cable.Point point : cable.getPoints()) {
                cablePoints.add(point.getPosition());
                prevCablePoints.add(point.getPrevPosition());
            }
            cablePoints.add(cable.getPoints().get(size).getPosition());
            prevCablePoints.add(cable.getPoints().get(size).getPrevPosition());
            int numSegments = 4;
            float segmentSize = 1f/numSegments;
            List<Vec3> points = CatmulRomSpline.generateSpline(cablePoints, numSegments);
            List<Vec3> prevPoints = CatmulRomSpline.generateSpline(prevCablePoints, numSegments);
            for (int i = 0; i < points.size(); i++) {
                float uv1 = i+1 % (numSegments+1) * segmentSize;
                float uv2 = uv1 + segmentSize;
                Vec3 point;
                Vec3 prevPoint;
                Vec3 nextPoint;
                Vec3 nextPrevPoint;
                if (i < cable.getPoints().size() - 1) {
                    point = points.get(i);
                    nextPoint = points.get(i + 1);
                    prevPoint = prevPoints.get(i);
                    nextPrevPoint = prevPoints.get(i + 1);
                } else {
                    point = points.get(i);
                    nextPoint = points.get(i - 1);
                    prevPoint = prevPoints.get(i);
                    nextPrevPoint = prevPoints.get(i - 1);
                }
                Vec3 normal = getPointsNormal(point, nextPoint);
                Vec3 startNormal = normal;
                if (i != 0)
                    startNormal = getPointsNormal(points.get(i-1),point);
                renderCableFrustrum(poseStack, cable.getColor(), uv1, uv2, SuperpositionConstants.cableWidth + (i % 5 * .0001f), Mth.lerpVec3(prevPoint, point, partialTicks), startNormal, Mth.lerpVec3(nextPrevPoint, nextPoint, partialTicks).add(normal.scale(.01f)), normal);
            }

            poseStack.popPose();
        }
        poseStack.popPose();
        vertexConsumer = null;
    }

    public static void renderCableSegment(
            PoseStack ps, Color color,
            double width, Vec3 startPosition, Vec3 startNormal, Vec3 endPosition, Vec3 endNormal
    ) {

    }

    public static void renderCableHeldPoint(LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, float partialTicks, Camera camera, Frustum frustum) {
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, CABLE);
        ClientLevel level = Minecraft.getInstance().level;
        Matrix4f matrix4f = poseStack.last().pose();
        Vec3 translation = camera.getPosition().scale(-1);
        poseStack.translate(translation.x, translation.y, translation.z);
        float width = 0.12f;
        for (Cable cable : CableManager.getLevelCables(level)) {
            poseStack.pushPose();
            for (UUID uuid : cable.getPlayerHoldingPointMap().keySet()) {
                int i = cable.getPlayerHoldingPointMap().get(uuid);
                Player player = level.getPlayerByUUID(uuid);

                Vec3 pointPos = cable.getPoints().get(i).getPosition();
                Vec3 prevPos = cable.getPoints().get(i).getPrevPosition();
                Vec3 pos = Mth.lerpVec3(prevPos, pointPos, partialTicks);
                if (Minecraft.getInstance().player.equals(player)) {
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, new AABB(pos.x - width, pos.y - width, pos.z - width, pos.x + width, pos.y + width, pos.z + width), 0.5f + stretch / 2, 0.9f - stretch / 2, 0.5f - stretch / 5, 0f + stretch / 2);
                    width += stretch / 32;
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, new AABB(pos.x - width, pos.y - width, pos.z - width, pos.x + width, pos.y + width, pos.z + width), 0.5f + stretch / 2, 0.9f - stretch / 2, 0.5f - stretch / 6, 0.5f + stretch / 6);
                } else
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, new AABB(pos.x - width, pos.y - width, pos.z - width, pos.x + width, pos.y + width, pos.z + width), 0.5f, 0.9f, 0.5f, 0.5f);
            }
            poseStack.popPose();
        }
        if (detachDelta > 0) {
            float delta = net.minecraft.util.Mth.lerp(partialTicks, detachDelta, detachDelta - 0.2f);
            stretch = 1;
            width = 0.12f - Mth.getFromRange(1, 0, 0, 1, delta) * 0.15125f;
            if (width > 0) {
                poseStack.pushPose();
                DebugRenderer.renderFilledBox(poseStack, bufferSource, new AABB(detachPos.x - width, detachPos.y - width, detachPos.z - width, detachPos.x + width, detachPos.y + width, detachPos.z + width), 1f, 0.4f, 0.3f, 0.8f);
                width += 0.03125f;
                DebugRenderer.renderFilledBox(poseStack, bufferSource, new AABB(detachPos.x - width, detachPos.y - width, detachPos.z - width, detachPos.x + width, detachPos.y + width, detachPos.z + width), 1f, 0.4f, 0.3f, 0.5f);
                poseStack.popPose();
            }
        }
        poseStack.pushPose();
        CableClipResult cableClipResult = new CableClipResult(camera.getPosition(), 8, level);
        oshi.util.tuples.Pair<Cable, Cable.Point> cablePointPair = cableClipResult.rayCastForClosest(Minecraft.getInstance().player.getEyePosition().add(Minecraft.getInstance().player.getEyePosition().add(Minecraft.getInstance().player.getForward().subtract(Minecraft.getInstance().player.getEyePosition())).scale(5)), .7f);
        if (cablePointPair != null) {
            Vec3 pointPos = cablePointPair.getB().getPosition();
            Vec3 prevPos = cablePointPair.getB().getPrevPosition();
            Vec3 pos = Mth.lerpVec3(prevPos, pointPos, partialTicks);
            if (!cablePointPair.getA().getPlayerHoldingPointMap().containsKey(Minecraft.getInstance().player.getUUID()))
                DebugRenderer.renderFilledBox(poseStack, bufferSource, new AABB(pos.x - width, pos.y - width, pos.z - width, pos.x + width, pos.y + width, pos.z + width), 0.5f, 0.9f, 0.5f, 0.4f);
        }
        poseStack.popPose();
        poseStack.popPose();
    }

    public static Vec3 getPointsNormal(Vec3 from, Vec3 to) {
        return to.subtract(from).normalize();
    }

    public static void renderCableFrustrum(
            PoseStack ps, Color color,  float uv1, float uv2,
            double width, Vec3 startPosition, Vec3 startNormal, Vec3 endPosition, Vec3 endNormal
    ) {
        if (startNormal.dot(endNormal) < 0)
            startNormal = startNormal.multiply(-1, -1, -1);

        Vec3 direction = endPosition.subtract(startPosition).normalize();

        List<Vec3> startCorners = getCornersFromNormal(direction, startPosition, startNormal, width);
        List<Vec3> endCorners = getCornersFromNormal(direction, endPosition, endNormal, width);

        int light = Math.max(LevelRenderer.getLightColor(Minecraft.getInstance().level, BlockPos.containing(endPosition)), LevelRenderer.getLightColor(Minecraft.getInstance().level, BlockPos.containing(startPosition)));


        ps.pushPose();

        //Render both sides because I don't know why normals are being a bit strange
//        Camera camera = getCamera();
//        ps.mulPoseMatrix(new Matrix4f().rotation(camera.rotation()));

        for (int index = 0; index < 4; index++) {
            int nextIndex = (index + 1) % 4;


            renderBeamPlane(
                    color,
                    light,
                    uv1,
                    uv2,
                    ps,
                    endCorners.get(index),
                    startCorners.get(index),
                    startCorners.get(nextIndex),
                    endCorners.get(nextIndex)
            );

            renderBeamPlane(
                    color,
                    light,
                    uv1,
                    uv2,
                    ps,
                    endCorners.get(nextIndex),
                    startCorners.get(nextIndex),
                    startCorners.get(index),
                    endCorners.get(index)
            );

        }

        renderBeamPlane(
                color,
                light,
                uv1,
                uv2,
                ps,
                startCorners.get(0),
                startCorners.get(1),
                startCorners.get(2),
                startCorners.get(3)
        );

        renderBeamPlane(
                color,
                light,
                uv1,
                uv2,
                ps,
                endCorners.get(0),
                endCorners.get(1),
                endCorners.get(2),
                endCorners.get(3)
        );

        ps.popPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);

    }

    @NotNull
    private static Camera getCamera() {
        return Minecraft.getInstance().gameRenderer.getMainCamera();
    }

    private static void renderBeamPlane(Color color, int light, float uv1, float uv2, PoseStack ps, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
        consumeVectorsVertex(color, light, uv1, uv2, ps, v1, v2, v3, v4);
    }

    private static void consumeVectorsVertex(Color color, int light, float uv1, float uv2, PoseStack ps, Vec3... vectors) {
        Vec3 normal = vectors[0].cross(vectors[1]);

        Matrix4f m = ps.last().pose();
        Vec2[] uvCorners = new Vec2[]{new Vec2(1, uv1), new Vec2(1, uv2), new Vec2(0, uv2), new Vec2(0, uv1)};
        int step = 0;
        for (Vec3 vec3 : vectors) {
            vertexConsumer.vertex(m, (float) vec3.x, (float) vec3.y, (float) vec3.z)
                    .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f)
                    .uv(uvCorners[step].x, uvCorners[step].y)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(light)
                    .normal((float) 0, (float) 0, (float) 0)
                    .endVertex();
            step++;
        }
    }

    public static List<Vec3> getCornersFromNormal(Vec3 direction, Vec3 position, Vec3 normal, double sideWith) {
        //Trig! My favorite - said nobody, like, ever

        Pair<Double, Double> directionPitchRoll = getYawAndPitch(direction);

        return Stream.of(
                new Vec3(0, 0, 0),
                new Vec3(0, 1, 0),
                new Vec3(1, 1, 0),
                new Vec3(1, 0, 0)
        ).map(vec3 -> {
            vec3 = vec3.subtract(new Vec3(0.5, 0.5, 0))
                    .xRot((float) (double) directionPitchRoll.getSecond())
                    .yRot((float) (double) directionPitchRoll.getFirst())
                    .scale(sideWith);
            //Intersect to the normal
            double intersectLength =
                    (normal.scale(-1).dot(vec3))
                            / normal.dot(direction);
            return vec3
                    .add(direction.scale(intersectLength))
                    .add(position);
        }).toList();

    }

    private static Pair<Double, Double> getYawAndPitch(Vec3 direction) {
//        direction = suppressRoundingErrors(direction);

        double yaw_s1 = direction.x;
        double yaw_s2 = direction.z;

        double yaw = Math.atan2(yaw_s1, yaw_s2);

        double pitch_s1 = Math.sqrt(Math.pow(yaw_s1, 2) + Math.pow(yaw_s2, 2));
        double pitch_s2 = direction.y;

        double pitch = Math.PI + Math.atan2(pitch_s2, pitch_s1);

        return new Pair<>(yaw, pitch);
    }

    private static Vec3 round(Vec3 vec) {
        return new Vec3(
                Math.round(vec.x),
                Math.round(vec.y),
                Math.round(vec.z)
        );
    }

    public static void drawPosBox(PoseStack poseStack, VertexConsumer vertexConsumer, Vec3 pos, float width, float red, float green, float blue) {
        float x1 = (float) pos.x - width;
        float y1 = (float) pos.y - width;
        float z1 = (float) pos.z - width;
        float x2 = (float) pos.x + width;
        float y2 = (float) pos.y + width;
        float z2 = (float) pos.z + width;
        renderBox(poseStack, vertexConsumer, x1, y1, z1, x2, y2, z2, red, green, blue, 1.0F, red, green, blue);
    }

    public static void renderBox(PoseStack poseStack, VertexConsumer builder, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha, float red2, float green2, float blue2) {
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        float f = (float) minX;
        float g = (float) minY;
        float h = (float) minZ;
        float i = (float) maxX;
        float j = (float) maxY;
        float k = (float) maxZ;

        builder.vertex(f, 1, 1).endVertex(); // Front-top-left
        builder.vertex(1, 1, 1).endVertex(); // Front-top-right
        builder.vertex(f, -1, 1).endVertex(); // Front-bottom-left
        builder.vertex(1, -1, 1).endVertex(); // Front-bottom-right
        builder.vertex(1, -1, -1).endVertex(); // Back-bottom-right
        builder.vertex(1, 1, 1).endVertex(); // Front-top-right
        builder.vertex(1, 1, -1).endVertex(); // Back-top-right
        builder.vertex(-1, 1, 1).endVertex(); // Front-top-left
        builder.vertex(-1, 1, -1).endVertex(); // Back-top-left
        builder.vertex(-1, -1, 1).endVertex(); // Front-bottom-left
        builder.vertex(-1, -1, -1).endVertex(); // Back-bottom-left
        builder.vertex(1, -1, -1).endVertex(); // Back-bottom-right
        builder.vertex(-1, 1, -1).endVertex(); // Back-top-left
        builder.vertex(1, 1, -1).endVertex(); // Back-top-right
    }

    // The minimum axis deviance before it is rounded and counted as an inaccuracy
    final static double ROUNDING_SENSITIVITY = 0.01;

    private static Vec3 suppressRoundingErrors(Vec3 vec) {
        Vec3 rounded = round(vec);

        // How close each axis is to the general direction
        Vec3 axisDeviance = rounded.subtract(vec);

        return new Vec3(
                axisDeviance.x < ROUNDING_SENSITIVITY ? rounded.x : vec.x,
                axisDeviance.y < ROUNDING_SENSITIVITY ? rounded.y : vec.y,
                axisDeviance.z < ROUNDING_SENSITIVITY ? rounded.z : vec.z
        );
    }
}
