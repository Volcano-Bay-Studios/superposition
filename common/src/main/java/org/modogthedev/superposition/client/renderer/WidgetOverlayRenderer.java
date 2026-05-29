package org.modogthedev.superposition.client.renderer;

import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.compat.sable.SableCompat;
import org.modogthedev.superposition.system.widget.Widget;

public class WidgetOverlayRenderer {


    public static void renderOverlay(LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource, MatrixStack matrixStack, Matrix4fc projectionMatrix, Matrix4fc matrix4fc, int renderTick, DeltaTracker deltaTracker, Camera camera) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        HitResult hitResult = mc.hitResult;
        matrixStack.matrixPush();
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();
            Vec3 position = new Vec3(pos.getX(),pos.getY(),pos.getZ());
            position = SableCompat.tryTransform(level,position);
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PanelBlockEntity panel) {
                Vec3 location = blockHitResult.getLocation();
                Widget hit = panel.getHit(new Vector3d(location.x,location.y,location.z));
                if (hit != null) {
                    matrixStack.translate(position.x - camera.getPosition().x, position.y - camera.getPosition().y, position.z - camera.getPosition().z);
                    Quaternionf rotation = SableCompat.getRotation(level, pos.getCenter(), Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
                    if (rotation != null) {
                        matrixStack.toPoseStack().mulPose(rotation);
                    }
                    matrixStack.toPoseStack().mulPose(panel.getPanelMatrix());
                    float shrink = 1/256f;
                    LevelRenderer.renderLineBox(matrixStack.toPoseStack(), bufferSource.getBuffer(RenderType.LINES), hit.getPosition().x /16f + shrink, 8/16f, hit.getPosition().y / 16f + shrink, hit.getPosition().x /16f + hit.getBounds().x - shrink, hit.getBounds().y + 9/16f - shrink, hit.getPosition().y /16f + hit.getBounds().z - shrink, 0.8f, 0.8f, 0.8f, 0.8f);
                }
            }
        }
        matrixStack.matrixPop();
    }
}