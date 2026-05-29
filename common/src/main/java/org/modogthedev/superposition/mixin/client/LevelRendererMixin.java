package org.modogthedev.superposition.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.modogthedev.superposition.compat.sable.SableCompat;
import org.modogthedev.superposition.util.block.DynamicShapedBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
                Vec3 position = new Vec3(pos.getX(),pos.getY(),pos.getZ());
                position = SableCompat.tryTransform(level,position);
                poseStack.translate(position.x - camX, position.y - camY, position.z - camZ);
                Quaternionf rotation = SableCompat.getRotation(level, pos.getCenter(), Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
                if (rotation != null) {
                    poseStack.mulPose(rotation);
                }
                poseStack.mulPose(shape.transformation());
                renderShape(poseStack, consumer, shape.shape(), 0, 0, 0, 0.0F, 0.0F, 0.0F, 0.4F);
                poseStack.popPose();
            }
        }

    }
}
