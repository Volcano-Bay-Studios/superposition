package org.modogthedev.superposition.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.FilterBlockEntity;
import org.modogthedev.superposition.core.SuperpositionRenderTypes;

import java.awt.*;

public class FilterBlockEntityRenderer implements BlockEntityRenderer<FilterBlockEntity> {

    public FilterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FilterBlockEntity be, float pPartialTick, PoseStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        if (isInvalid(be))
            return;
        if (be.getFilter() == null)
            return;
        VertexConsumer buffer = bufferSource.getBuffer(SuperpositionRenderTypes.bloomBlockPolygonOffset(Superposition.id("textures/screen/filter_block_screen.png")));

        float min = getMinPlaneExtent(be);
        float max = getMaxPlaneExtent(be);
        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);
        ms.mulPose(be.getBlockState().getValue(SignalGeneratorBlock.FACING).getRotation());
        ms.translate(0, -.125, 0.03f);


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
        Color color = be.getFilter().getColor();
        float offset = (stage / stages) + .5f;

        light = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(be.getBlockState().getValue(SignalGeneratorBlock.FACING), 1));

        buffer
                .addVertex(m, -0.1887f, 0.5001f, -0.15525f)
                .setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .setUv(0, (uvMin / stages) + offset)
                .setLight(light)
                .setNormal(ms.last(), 0, 1, 0);

        buffer
                .addVertex(m, -0.1887f, 0.5001f, 0.21825f)
                .setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .setUv(0, (uvMin / stages) + offset)
                .setLight(light)
                .setNormal(ms.last(), 0, 1, 0);

        buffer
                .addVertex(m, 0.1887f, 0.5001f, 0.21825f)
                .setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .setUv(1, (uvMin / stages) + offset)
                .setLight(light)
                .setNormal(ms.last(), 0, 1, 0);

        buffer
                .addVertex(m, 0.1887f, 0.5001f, -0.15525f)
                .setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .setUv(1, (uvMin / stages) + offset)
                .setLight(light)
                .setNormal(ms.last(), 0, 1, 0);
        ms.popPose();

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
