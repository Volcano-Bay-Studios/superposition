package org.modogthedev.superposition.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.AmplifierBlockEntity;
import org.modogthedev.superposition.blockentity.SpotlightBlockEntity;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.core.SuperpositionRenderTypes;

import java.awt.*;

public class SpotlightBlockEntityRenderer implements BlockEntityRenderer<SpotlightBlockEntity> {

    public SpotlightBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    private static RenderType renderType = null;

    @Override
    public void render(SpotlightBlockEntity be, float pPartialTick, PoseStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        if (renderType == null) {
            if (SuperpositionConstants.bloomEnabled) {
                renderType = SuperpositionRenderTypes.bloomBlockPolygonOffset(Superposition.id("textures/screen/spotlight_block_screen.png"));
            } else {
                renderType = SuperpositionRenderTypes.blockPolygonOffset(Superposition.id("textures/screen/spotlight_block_screen.png"));
            }
        }
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        float min = getMinPlaneExtent(be);
        float max = getMaxPlaneExtent(be);
        Color color = new Color(be.getColor());

        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);
        ms.mulPose(be.getBlockState().getValue(SignalGeneratorBlock.FACING).getRotation());

        if (be.getBlockState().getValue(SignalGeneratorBlock.FACING) == Direction.DOWN) {
            ms.translate(0,-12/16f,0);
        } else if (be.getBlockState().getValue(SignalGeneratorBlock.FACING) == Direction.UP) {
            ms.translate(0,-12/16f,0);
        } else {
            ms.translate(0, 0f, 0.06f);
        }


        Matrix4f m = ms.last().pose();
        Matrix3f n = ms.last().normal();

        float uvMin = (-0.5f);
        float uvMax = (.5f);


        float alpha = 1 /*Mth.clamp(be.remainingPolishAmount / UnpolishedComponentBlockEntity.DEFAULT_POLISHING_AMOUNT, 0f, 1f)*/;


        light = LightTexture.FULL_BRIGHT; //TODO: make the light pulse

        buffer
                .addVertex(m, -max, 0.5001f, -min)
                .setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .setUv(0, 0)
                .setLight(light)
                .setNormal(ms.last(), 0, 1, 0);

        buffer
                .addVertex(m, -max, 0.5001f, min)
                .setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .setUv(0, 1)
                .setLight(light)
                .setNormal(ms.last(), 0, 1, 0);

        buffer
                .addVertex(m, max, 0.5001f, min)
                .setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .setUv(1, 1)
                .setLight(light)
                .setNormal(ms.last(), 0, 1, 0);

        buffer
                .addVertex(m, max, 0.5001f, -min)
                .setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha)
                .setUv(1, 0)
                .setLight(light)
                .setNormal(ms.last(), 0, 1, 0);
        ms.popPose();
    }

    private float getMaxPlaneExtent(SpotlightBlockEntity be) {
        return 0.31f;
    }

    private float getMinPlaneExtent(SpotlightBlockEntity be) {
        return 0.31f;
    }

    public boolean isInvalid(AmplifierBlockEntity be) {
        return !be.hasLevel() || be.getBlockState()
                .getBlock() == Blocks.AIR;
    }

    @Override
    public boolean shouldRender(SpotlightBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return (BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos));
    }
}
