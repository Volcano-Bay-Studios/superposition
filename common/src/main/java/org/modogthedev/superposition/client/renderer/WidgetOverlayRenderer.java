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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4fc;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.system.widget.Widget;

public class WidgetOverlayRenderer {
    public static void renderOverlay(LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource, MatrixStack matrixStack, Matrix4fc projectionMatrix, Matrix4fc matrix4fc, int renderTick, DeltaTracker deltaTracker, Camera camera) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        HitResult hitResult = mc.hitResult;
        matrixStack.matrixPush();
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PanelBlockEntity panel) {
                Widget hit = panel.getHit(blockHitResult.getLocation().toVector3f());
                if (hit != null) {
                    matrixStack.translate(pos.getX() - camera.getPosition().x, pos.getY() - camera.getPosition().y, pos.getZ() - camera.getPosition().z);
                    matrixStack.toPoseStack().mulPose(panel.getPanelMatrix());
                    LevelRenderer.renderLineBox(matrixStack.toPoseStack(), bufferSource.getBuffer(RenderType.LINES), hit.getPosition().x, 9/16f + 1/816f, hit.getPosition().y, hit.getPosition().x + hit.getBounds().x, hit.getBounds().y + 9/16f, hit.getPosition().y + hit.getBounds().z, 0.2f, 0.8f, 0.2f, 0.6f);
                }
            }
        }
        matrixStack.matrixPop();
    }
}