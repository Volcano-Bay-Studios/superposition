package org.modogthedev.superposition.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.AmplifierBlockEntity;
import org.modogthedev.superposition.core.SuperpositionRenderTypes;

public class AmplifierBlockEntityRenderer implements BlockEntityRenderer<AmplifierBlockEntity> {

    public AmplifierBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }
    @Override
    public void render(AmplifierBlockEntity be, float pPartialTick, PoseStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        if (isInvalid(be))
            return;
        if (be.ticks == -1)
            return;
        VertexConsumer buffer = bufferSource.getBuffer(SuperpositionRenderTypes.polygonOffset(Superposition.id("textures/screen/amplifier_block_screen.png")));

        float min = getMinPlaneExtent(be);
        float max = getMaxPlaneExtent(be);

        ms.translate(0.5, 0.5, 0.5);
        ms.mulPose(be.getBlockState().getValue(SignalGeneratorBlock.FACING).getRotation());
        ms.translate(0,-.125,0.03f);


        Matrix4f m = ms.last().pose();
        Matrix3f n = ms.last().normal();

        float uvMin = (-0.5f);
        float uvMax = (.5f);

        float alpha = 1 /*Mth.clamp(be.remainingPolishAmount / UnpolishedComponentBlockEntity.DEFAULT_POLISHING_AMOUNT, 0f, 1f)*/ ;
        float stage = be.step;
        float stages = 3;

        float offset = (stage / stages)+.5f;

        light = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(be.getBlockState().getValue(SignalGeneratorBlock.FACING),1));

        buffer
                .vertex(m, -0.1887f, 0.5001f, -0.15625f)
                .color(1f, 1f, 1f, alpha)
                .uv(0, (uvMin/stages)+offset)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, -0.1887f, 0.5001f, 0.15825f)
                .color(1f, 1f, 1f, alpha)
                .uv(0, (uvMax/stages)+offset)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 0.1887f, 0.5001f, 0.15825f)
                .color(1f, 1f, 1f, alpha)
                .uv(1, (uvMax/stages)+offset)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 0.1887f, 0.5001f, -0.15625f)
                .color(1f, 1f, 1f, alpha)
                .uv(1, (uvMin/stages)+offset)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(n, 0, 1, 0)
                .endVertex();

    }
    private float getMaxPlaneExtent(AmplifierBlockEntity be) {
        return -(0.1875f);
    }

    private float getMinPlaneExtent(AmplifierBlockEntity be) {
        return 0.1875f;
    }
    public boolean isInvalid(AmplifierBlockEntity be) {
        return !be.hasLevel() || be.getBlockState()
                .getBlock() == Blocks.AIR;
    }

    @Override
    public boolean shouldRender(AmplifierBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return (BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos));
    }
}
