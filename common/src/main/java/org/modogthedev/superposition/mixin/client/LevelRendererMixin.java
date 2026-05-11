package org.modogthedev.superposition.mixin.client;

import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.util.DynamicShapedBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    private static void renderShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha) {
    }

    @Inject(method = "renderHitOutline", at = @At("HEAD"))
    private void renderHitOutline(PoseStack poseStack, VertexConsumer consumer, Entity entity, double camX, double camY, double camZ, BlockPos pos, BlockState state, CallbackInfo ci) {
        Level level = entity.level();

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DynamicShapedBlockEntity shapedBlockEntity) {
            for (DynamicShapedBlockEntity.DynamicShape shape : shapedBlockEntity.getShapes(true)) {
                poseStack.pushPose();
                poseStack.translate(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ);
                poseStack.mulPose(shape.transformation());
                renderShape(poseStack, consumer, shape.shape(), 0, 0, 0, 0.0F, 0.0F, 0.0F, 0.4F);
                poseStack.popPose();
            }
        }

    }
}
