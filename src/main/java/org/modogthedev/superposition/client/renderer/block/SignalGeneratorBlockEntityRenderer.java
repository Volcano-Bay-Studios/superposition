package org.modogthedev.superposition.client.renderer.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.SignalGeneratorBlockEntity;

import java.util.Objects;

public class SignalGeneratorBlockEntityRenderer implements BlockEntityRenderer<SignalGeneratorBlockEntity> {

    public SignalGeneratorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }
    @Override
    public void render(SignalGeneratorBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        Direction direction = pBlockEntity.getBlockState().getValue(SignalGeneratorBlock.FACING);
        pPoseStack.translate(0.5D, 0.0D, 0.5D);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - direction.toYRot()));
        pPoseStack.translate(-0.5D, 0.0D, -0.5D);
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.cutoutMipped());
        pPoseStack.popPose();
    }
}
