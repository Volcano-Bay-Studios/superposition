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
import org.modogthedev.superposition.blockentity.FilterBlockEntity;
import org.modogthedev.superposition.core.SuperpositionRenderTypes;
import org.modogthedev.superposition.item.FilterItem;

import java.awt.*;

public class FilterBlockEntityRenderer  implements BlockEntityRenderer<FilterBlockEntity> {

    public FilterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }
    @Override
    public void render(FilterBlockEntity be, float pPartialTick, PoseStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        if (isInvalid(be))
            return;
        if (be.getFilterType() == null)
            return;
        VertexConsumer buffer = bufferSource.getBuffer(SuperpositionRenderTypes.polygonOffset(Superposition.id("textures/screen/filter_block_screen.png")));

        float min = getMinPlaneExtent(be);
        float max = getMaxPlaneExtent(be);

        ms.translate(0.5, 0.5, 0.5);
        ms.mulPose(be.getBlockState().getValue(SignalGeneratorBlock.FACING).getRotation());
        ms.translate(0,-.125,0.03f);


        Matrix4f m = ms.last().pose();
        Matrix3f n = ms.last().normal();

        float uvMin = (-0.5f);
        float uvMax = (.5f);

        float alpha = 1;
        float stage = 1;
//        switch (be.getFilterType()) {
//            case LOW_PASS ->
//                stage = 1;
//            case HIGH_PASS ->
//                stage = 3;
//            case BAND_PASS ->
//                stage = 2;
//        }
        float stages = 1;
        Color color = be.getFilterType().getColor();
        float offset = (stage / stages)+.5f;

        light = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(be.getBlockState().getValue(SignalGeneratorBlock.FACING),1));

        buffer
                .vertex(m, -0.1887f, 0.5001f, -0.15525f)
                .color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, alpha)
                .uv(0, (uvMin/stages)+offset)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, -0.1887f, 0.5001f, 0.21825f)
                .color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, alpha)
                .uv(0, (uvMax/stages)+offset)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 0.1887f, 0.5001f, 0.21825f)
                .color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, alpha)
                .uv(1, (uvMax/stages)+offset)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(n, 0, 1, 0)
                .endVertex();

        buffer
                .vertex(m, 0.1887f, 0.5001f, -0.15525f)
                .color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, alpha)
                .uv(1, (uvMin/stages)+offset)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(n, 0, 1, 0)
                .endVertex();

    }
    private float getMaxPlaneExtent(FilterBlockEntity be) {
        return -(0.1875f);
    }

    private float getMinPlaneExtent(FilterBlockEntity be) {
        return 0.1875f;
    }
    public boolean isInvalid(FilterBlockEntity be) {
        return !be.hasLevel() || be.getBlockState()
                .getBlock() == Blocks.AIR;
    }

    @Override
    public boolean shouldRender(FilterBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return (BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos));
    }
}
