package org.modogthedev.superposition.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.vertex.VertexArray;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.blockentity.AntennaActorBlockEntity;
import org.modogthedev.superposition.compat.sable.SableCompat;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.core.SuperpositionRenderTypes;
import org.modogthedev.superposition.system.antenna.type.PhysicalAntenna;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableClientState;
import org.modogthedev.superposition.system.cable.CableClipResult;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.rope_system.RopeNode;
import org.modogthedev.superposition.util.CatmulRomSpline;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class CableRenderer {

    private static final Quaternionf POSITIVE_Y = new Quaternionf().setAngleAxis(Math.PI / 2, 1, 0, 0);
    private static final Quaternionf NEGATIVE_Y = new Quaternionf().setAngleAxis(-Math.PI / 2, 1, 0, 0);
    private static final Matrix4f FRUSTUM = new Matrix4f();
    private static final Matrix4f PROJECTION = new Matrix4f();

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

    public static void renderCable(Cable cable, CableClientState clientState, VertexConsumer vertexConsumer, BlockAndTintGetter level, float partialTicks) {
        Vector3dc origin = clientState.getOrigin();

        CABLE_POINTS.clear();
        RopeNode firstPoint = cable.getPoints().getFirst();
        Vec3 firstPos = firstPoint.getPosition();
        CABLE_POINTS.add(firstPos);
        if (cable.getPoints().getFirst().getAnchor() != null) {
            RopeNode point = cable.getPoints().getFirst();
            Vec3 normal = SableCompat.transformNormal((Level) level, point.getAnchor().getAnchorBlock().getCenter() ,Vec3.atLowerCornerOf(point.getAnchor().getDirection().getNormal())).scale(-0.2f);
            Vec3 pos = SableCompat.tryTransform((Level) level, point.getAnchor().getAnchorBlock().getCenter()).add(normal);

            CABLE_POINTS.add(pos);
        }

        for (RopeNode point : cable.getPoints()) {
            Vec3 pos = point.getRenderPosition(partialTicks);
            CABLE_POINTS.add(pos);
        }

        if (cable.getPoints().getLast().getAnchor() != null) {
            RopeNode point = cable.getPoints().getLast();
            Vec3 normal = SableCompat.transformNormal((Level) level, point.getAnchor().getAnchorBlock().getCenter() ,Vec3.atLowerCornerOf(point.getAnchor().getDirection().getNormal())).scale(-0.2f);
            Vec3 pos = SableCompat.tryTransform((Level) level, point.getAnchor().getAnchorBlock().getCenter()).add(normal);

            CABLE_POINTS.add(pos);
        }
        RopeNode lastPoint = cable.getPoints().getLast();
        Vec3 lastPos = lastPoint.getPosition();
        CABLE_POINTS.add(lastPos);
        List<Vec3> splinePoints = CatmulRomSpline.generateSpline(CABLE_POINTS, SuperpositionConstants.cableSegments);

        int color = 0xFF000000 | cable.getColor().getRGB();
        float constantRadius = SuperpositionConstants.cableWidth / 2.0f;
        float v = 0;
        float nextV;

        renderCableStart(vertexConsumer, level, origin, color, splinePoints.getFirst(), splinePoints.get(1));

        for (int i = 0; i < splinePoints.size() - 1; i++) {
            float delta = (float) i / (splinePoints.size() - 1);
            float nextDelta = (float) (i + 1) / (splinePoints.size() - 1);
            float cableRadius = constantRadius - (0.001f * delta);
            float nextCableRadius = constantRadius - (0.001f * nextDelta);
            Vec3 point = splinePoints.get(i);
            Vec3 nextPoint = splinePoints.get(i + 1);

            double x = point.x;
            double y = point.y;
            double z = point.z;
            double nextX = nextPoint.x;
            double nextY = nextPoint.y;
            double nextZ = nextPoint.z;

            if (i < splinePoints.size() - 2) {
                if (i == 0) {
                    calculateOrientation(NEXT_ORIENTATION, nextX, nextY, nextZ, splinePoints.get(i + 2));
                } else {
                    updateOrientation(NEXT_ORIENTATION,splinePoints.get(i),splinePoints.get(i+1),splinePoints.get(i+2));
                }
            } else {
                NEXT_ORIENTATION.set(ORIENTATION);
            }

            int lightStart = LevelRenderer.getLightColor(level, LIGHT_POS.set(x, y, z));
            int lightEnd = LevelRenderer.getLightColor(level, LIGHT_POS.set(nextX, nextY, nextZ));
            double length = Math.sqrt((nextX - x) * (nextX - x) + (nextY - y) * (nextY - y) + (nextZ - z) * (nextZ - z));
            nextV = v + (float) (length * 16.0 / 6.0);

            Vec3[] corners = {
                    new Vec3(-1, -1, 0),
                    new Vec3( 1, -1, 0),
                    new Vec3( 1,  1, 0),
                    new Vec3(-1,  1, 0)
            };

            Vec3[] faceNormals = {
                    new Vec3(0, -1, 0),
                    new Vec3(1, 0, 0),
                    new Vec3(0, 1, 0),
                    new Vec3(-1, 0, 0)
            };

            for (int side = 0; side < 4; side++) {
                int nextSide = (side + 1) % 4;

                ORIENTATION.transform(NORMAL.set(faceNormals[side].x, faceNormals[side].y, faceNormals[side].z));
                NEXT_ORIENTATION.transform(NEXT_NORMAL.set(faceNormals[side].x, faceNormals[side].y, faceNormals[side].z));

                Vec3 c1 = corners[side];
                Vec3 c2 = corners[nextSide];

                ORIENTATION.transform(POS.set(c1.x * cableRadius, c1.y * cableRadius, 0));
                addCableVertex(vertexConsumer, origin, x, y, z, POS, color, 0, v, lightStart, NORMAL);

                ORIENTATION.transform(POS.set(c2.x * cableRadius, c2.y * cableRadius, 0));
                addCableVertex(vertexConsumer, origin, x, y, z, POS, color, 0.5f, v, lightStart, NORMAL);

                NEXT_ORIENTATION.transform(POS.set(c2.x * nextCableRadius, c2.y * nextCableRadius, 0));
                addCableVertex(vertexConsumer, origin, nextX, nextY, nextZ, POS, color, 0.5f, nextV, lightEnd, NEXT_NORMAL);

                NEXT_ORIENTATION.transform(POS.set(c1.x * nextCableRadius, c1.y * nextCableRadius, 0));
                addCableVertex(vertexConsumer, origin, nextX, nextY, nextZ, POS, color, 0, nextV, lightEnd, NEXT_NORMAL);
            }

            ORIENTATION.set(NEXT_ORIENTATION);
            v = nextV;
        }
        renderCableEnd(vertexConsumer, level, origin, color, splinePoints.getLast(), splinePoints.get(splinePoints.size() - 2));
    }

    public static void addCableVertex(VertexConsumer consumer, Vector3dc origin, double x, double y, double z,
                                Vector3f pos, int color, float u, float v, int light, Vector3f normal) {
        consumer.addVertex((float) (x - origin.x() + pos.x),
                        (float) (y - origin.y() + pos.y),
                        (float) (z - origin.z() + pos.z))
                .setColor(color)
                .setUv(u, v)
                .setLight(light)
                .setNormal(normal.x, normal.y, normal.z);
    }

    public static void renderCables(Matrix4fc projectionMatrix, Matrix4fc frustumMatrix, DeltaTracker deltaTracker, Camera camera) {
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(true);
        ClientLevel level = Minecraft.getInstance().level;
        Vec3 cameraPos = camera.getPosition();
        RenderType renderType = SuperpositionRenderTypes.cable();

        renderType.setupRenderState();
        ShaderInstance shader = RenderSystem.getShader();
        if (shader == null) {
            renderType.clearRenderState();
            return;
        }

        shader.apply();
        for (Cable cable : CableManager.getLevelCables(level)) {
            CableClientState renderState = cable.getRenderState(cable.isSleeping() ? 1.0F : partialTicks);
            Vector3dc origin = renderState.getOrigin();

            if (shader.CHUNK_OFFSET != null) {
                shader.CHUNK_OFFSET.set((float) (origin.x() - cameraPos.x), (float) (origin.y() - cameraPos.y), (float) (origin.z() - cameraPos.z));
                shader.CHUNK_OFFSET.upload();
            }
            renderState.render(shader, FRUSTUM.set(frustumMatrix), PROJECTION.set(projectionMatrix));
        }

        if (shader.CHUNK_OFFSET != null) {
            shader.CHUNK_OFFSET.set(0.0F, 0.0F, 0.0F);
        }
        VertexArray.unbind();
        shader.clear();
        renderType.clearRenderState();
    }

    private static void renderCableStart(VertexConsumer vertexConsumer, BlockAndTintGetter level, Vector3dc cameraPos, int color, Vec3 point, Vec3 nextPoint) {
        float cableRadius = (SuperpositionConstants.cableWidth / 2.0f);
        double x = point.x;
        double y = point.y;
        double z = point.z;

        // TODO attach to block face
        calculateOrientation(ORIENTATION, x, y, z, nextPoint);

        // Draw first face
        int startLight = LevelRenderer.getLightColor(level, LIGHT_POS.set(x, y, z));
        ORIENTATION.transform(NORMAL.set(0, 0, -1));
        ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
        vertexConsumer.addVertex((float) (x - cameraPos.x() + POS.x), (float) (y - cameraPos.y() + POS.y), (float) (z - cameraPos.z() + POS.z)).setColor(255, 255, 255, 255).setUv(0.5F, 0.5F).setLight(startLight).setNormal(NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
        vertexConsumer.addVertex((float) (x - cameraPos.x() + POS.x), (float) (y - cameraPos.y() + POS.y), (float) (z - cameraPos.z() + POS.z)).setColor(255, 255, 255, 255).setUv(0.5F, 1.0F).setLight(startLight).setNormal(NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
        vertexConsumer.addVertex((float) (x - cameraPos.x() + POS.x), (float) (y - cameraPos.y() + POS.y), (float) (z - cameraPos.z() + POS.z)).setColor(255, 255, 255, 255).setUv(1.0F, 1.0F).setLight(startLight).setNormal(NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
        vertexConsumer.addVertex((float) (x - cameraPos.x() + POS.x), (float) (y - cameraPos.y() + POS.y), (float) (z - cameraPos.z() + POS.z)).setColor(255, 255, 255, 255).setUv(1.0F, 0.5F).setLight(startLight).setNormal(NORMAL.x, NORMAL.y, NORMAL.z);
    }

    private static void renderCableEnd(VertexConsumer vertexConsumer, BlockAndTintGetter level, Vector3dc cameraPos, int color, Vec3 point, Vec3 prevNextPoint) {
        float cableRadius = (SuperpositionConstants.cableWidth / 2.0f) - 0.001f;
        double x = point.x;
        double y = point.y;
        double z = point.z;

        // TODO attach to block face
        calculateOrientation(ORIENTATION, x, y, z, prevNextPoint);
        ORIENTATION.rotateAxis((float) Math.PI, 0, 1, 0);

        // Draw first face
        int startLight = LevelRenderer.getLightColor(level, LIGHT_POS.set(x, y, z));
        ORIENTATION.transform(NORMAL.set(0, 0, 1));
        ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0));
        vertexConsumer.addVertex((float) (x - cameraPos.x() + POS.x), (float) (y - cameraPos.y() + POS.y), (float) (z - cameraPos.z() + POS.z)).setColor(255, 255, 255, 255).setUv(0.5F, 0.5F).setLight(startLight).setNormal(NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0));
        vertexConsumer.addVertex((float) (x - cameraPos.x() + POS.x), (float) (y - cameraPos.y() + POS.y), (float) (z - cameraPos.z() + POS.z)).setColor(255, 255, 255, 255).setUv(0.5F, 1.0F).setLight(startLight).setNormal(NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0));
        vertexConsumer.addVertex((float) (x - cameraPos.x() + POS.x), (float) (y - cameraPos.y() + POS.y), (float) (z - cameraPos.z() + POS.z)).setColor(255, 255, 255, 255).setUv(1.0F, 1.0F).setLight(startLight).setNormal(NORMAL.x, NORMAL.y, NORMAL.z);

        ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0));
        vertexConsumer.addVertex((float) (x - cameraPos.x() + POS.x), (float) (y - cameraPos.y() + POS.y), (float) (z - cameraPos.z() + POS.z)).setColor(255, 255, 255, 255).setUv(1.0F, 0.5F).setLight(startLight).setNormal(NORMAL.x, NORMAL.y, NORMAL.z);
    }

    private static void calculateOrientation(Quaternionf store, double x, double y, double z, Vec3 nextPoint) {
        double dx = (nextPoint.x - x);
        double dy = (nextPoint.y - y);
        double dz = (nextPoint.z - z);
        float factor = 0;//(float) Mth.smoothstep(1.0-Mth.clamp(8*Math.sqrt(dx * dx + dz * dz), 0.0, 1.0));
        store.identity().rotateAxis((float) Math.atan2(dx, dz), 0, 1, 0).rotateAxis((float) (Math.acos(dy / Math.sqrt(dx * dx + dy * dy + dz * dz)) - Math.PI / 2.0), 1, 0, 0).slerp(dy < 0 ? POSITIVE_Y : NEGATIVE_Y, factor);
    }

    public static void updateOrientation(Quaternionf currentFrame, Vec3 p1, Vec3 p2, Vec3 p3) {
        Vector3f t1 = new Vector3f((float)(p2.x - p1.x), (float)(p2.y - p1.y), (float)(p2.z - p1.z)).normalize();
        Vector3f t2 = new Vector3f((float)(p3.x - p2.x), (float)(p3.y - p2.y), (float)(p3.z - p2.z)).normalize();

        Vector3f axis = new Vector3f(t1).cross(t2);
        float length = axis.length();

        if (length > 0.0001f) {
            axis.normalize();
            float dot = t1.dot(t2);
            float angle = (float) Math.acos(Mth.clamp(dot, -1f, 1f));

            Quaternionf stepRotation = new Quaternionf().rotationAxis(angle, axis);
            currentFrame.premul(stepRotation);
        }
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

                    Vec3 pos = cable.getPoints().get(i).getRenderPosition(partialTicks);
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
        oshi.util.tuples.Pair<Cable, RopeNode> cablePointPair = cableClipResult.rayCastForClosest(CableManager.getPlayerHoldCablePos(Minecraft.getInstance().player), .7f, !Minecraft.getInstance().player.isShiftKeyDown());
        if (cablePointPair != null) {
            Vec3 pos = cablePointPair.getB().getRenderPosition(partialTicks);
            if (!cablePointPair.getA().getPlayerHoldingPointMap().containsKey(Minecraft.getInstance().player.getId())) {
                boolean isLast = cablePointPair.getA().getPoints().get(cablePointPair.getA().getPoints().size() - 1).equals(cablePointPair.getB());
                boolean isFirst = cablePointPair.getA().getPoints().get(0).equals(cablePointPair.getB());
                boolean hasAnchor = cablePointPair.getB().getAnchor() != null;

                Vector4f color = getColorForNodeHighlight(isLast, isFirst, hasAnchor);
                if (!Minecraft.getInstance().player.getAbilities().mayBuild) {
                    color = new Vector4f(0.5f, 0.5f, 0.5f, 0.5f);
                }
                if (isLast || isFirst) {
                    width -= 0.03f;
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, pos.x - cameraPos.x - width, pos.y - cameraPos.y - width, pos.z - cameraPos.z - width, pos.x - cameraPos.x + width, pos.y - cameraPos.y + width, pos.z - cameraPos.z + width, color.x, color.y, color.z, color.w);
                    width += 0.03f;
                    color.w = 0.4f;
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, pos.x - cameraPos.x - width, pos.y - cameraPos.y - width, pos.z - cameraPos.z - width, pos.x - cameraPos.x + width, pos.y - cameraPos.y + width, pos.z - cameraPos.z + width, color.x, color.y, color.z, color.w);
                    if (isFirst) {
                        DebugRenderer.renderFloatingText(poseStack, bufferSource, "Pull", pos.x, pos.y + 0.5f, pos.z, Superposition.SUPERPOSITION_THEME.get("topBorder"));
                    } else {
                        DebugRenderer.renderFloatingText(poseStack, bufferSource, "Push", pos.x, pos.y + 0.5f, pos.z, Superposition.SUPERPOSITION_THEME.get("topBorder"));
                    }
                } else {
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, pos.x - cameraPos.x - width, pos.y - cameraPos.y - width, pos.z - cameraPos.z - width, pos.x - cameraPos.x + width, pos.y - cameraPos.y + width, pos.z - cameraPos.z + width, color.x, color.y, color.z, color.w);
                }

                for (RopeNode node : cablePointPair.getA().getPoints()) {
                    if (node == cablePointPair.getB() || node.getAnchor() == null) continue;
                    Vec3 anchorPos = node.getRenderPosition(partialTicks);
                    DebugRenderer.renderFilledBox(poseStack, bufferSource, anchorPos.x - cameraPos.x - width, anchorPos.y - cameraPos.y - width, anchorPos.z - cameraPos.z - width, anchorPos.x - cameraPos.x + width, anchorPos.y - cameraPos.y + width, anchorPos.z - cameraPos.z + width, 0.4f, 0.4f, 0.9f, 0.2f);
                }
            }
        }
    }

    private static Vector4f getColorForNodeHighlight(boolean isLast, boolean isFirst, boolean hasAnchor) {
        if (isLast) {
            return new Vector4f(0.5f, 0.5f, 0.9f, 0.6f);
        } else if (isFirst) {
            return new Vector4f(0.9f, 0.5f, 0.5f, 0.6f);
        } else if (hasAnchor) {
            return new Vector4f(0.4f, 0.4f, 0.9f, 0.6f);
        } else {
            return new Vector4f(0.4f, 0.9f, 0.4f, 0.4f);
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
                if (antennaActorBlockEntity.antenna instanceof PhysicalAntenna physicalAntenna) {
                    for (BlockPos pos : physicalAntenna.antennaParts) {
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
