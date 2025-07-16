package org.modogthedev.superposition.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.SignalGeneratorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.core.SuperpositionRenderTypes;

public class SignalGeneratorBlockEntityRenderer implements BlockEntityRenderer<SignalGeneratorBlockEntity> {

    public SignalGeneratorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    private static RenderType renderType = null;

    @Override
    public void render(SignalGeneratorBlockEntity be, float pPartialTick, PoseStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        if (isInvalid(be))
            return;
        if (renderType == null) {
            if (SuperpositionConstants.bloomEnabled) {
                renderType = SuperpositionRenderTypes.bloomBlockPolygonOffset(Superposition.id("textures/block/signal_generator/front.png"));
            } else {
                renderType = SuperpositionRenderTypes.blockPolygonOffset(Superposition.id("textures/block/signal_generator/front.png"));
            }
        }
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        float min = getMinPlaneExtent(be);
        float max = getMaxPlaneExtent(be);

        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);
        ms.mulPose(be.getBlockState().getValue(SignalGeneratorBlock.FACING).getRotation());


        Matrix4f m = ms.last().pose();
        Matrix3f n = ms.last().normal();

        float uvMin = (.5f + min);
        float uvMax = (.5f + max);

        float alpha = 1 /*Mth.clamp(be.remainingPolishAmount / UnpolishedComponentBlockEntity.DEFAULT_POLISHING_AMOUNT, 0f, 1f)*/;
        float stage = Math.round(be.dial);
        float stages = 25;

        float offset = (stage / stages);
        float uvOffsetx = 0f;

        light = LightTexture.FULL_BRIGHT;

        buffer
                .addVertex(m, min, 0.5001f, min)
                .setColor(1f, 1f, 1f, alpha)
                .setUv(0 + uvOffsetx, (uvMin / stages) + offset)
                .setLight(light)
                .setNormal(ms.last(), 0, 1, 0);

        buffer
                .addVertex(m, min, 0.5001f, max)
                .setColor(1f, 1f, 1f, alpha)
                .setUv(0 + uvOffsetx, (uvMax / stages) + offset)
                .setLight(light)
                .setNormal(ms.last(), 0, 1, 0);

        buffer
                .addVertex(m, max, 0.5001f, max)
                .setColor(1f, 1f, 1f, alpha)
                .setUv(1 + uvOffsetx, (uvMax / stages) + offset)
                .setLight(light)
                .setNormal(ms.last(), 0, 1, 0);

        buffer
                .addVertex(m, max, 0.5001f, min)
                .setColor(1f, 1f, 1f, alpha)
                .setUv(1 + uvOffsetx, (uvMin / stages) + offset)
                .setLight(light)
                .setNormal(ms.last(), 0, 1, 0);
        ms.popPose();
    }

    private float getMaxPlaneExtent(SignalGeneratorBlockEntity be) {
        return -(0.5f);
    }

    private float getMinPlaneExtent(SignalGeneratorBlockEntity be) {
        return 0.5f;
    }

    public boolean isInvalid(SignalGeneratorBlockEntity be) {
        return !be.hasLevel() || be.getBlockState()
                .getBlock() == Blocks.AIR;
    }

    @Override
    public boolean shouldRender(SignalGeneratorBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return (BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos) && (pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos().relative(pBlockEntity.getBlockState().getValue(SignalGeneratorBlock.FACING), 1)).is(Blocks.AIR) || !pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos().relative(pBlockEntity.getBlockState().getValue(SignalGeneratorBlock.FACING), 1)).canOcclude()));
    }
}
