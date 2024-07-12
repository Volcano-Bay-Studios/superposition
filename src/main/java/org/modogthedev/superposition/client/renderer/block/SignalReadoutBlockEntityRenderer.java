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
import org.modogthedev.superposition.blockentity.SignalReadoutBlockEntity;
import org.modogthedev.superposition.core.SuperpositionRenderTypes;
import org.modogthedev.superposition.system.signal.Signal;

import static java.lang.Math.floor;

public class SignalReadoutBlockEntityRenderer implements BlockEntityRenderer<SignalReadoutBlockEntity> {

    public SignalReadoutBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    int size = 14;
    @Override
    public void render(SignalReadoutBlockEntity be, float pPartialTick, PoseStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        if (isInvalid(be))
            return;
        VertexConsumer buffer = bufferSource.getBuffer(SuperpositionRenderTypes.polygonOffset(Superposition.asResource("textures/screen/pixel.png")));

        float min = getMinPlaneExtent(be);
        float max = getMaxPlaneExtent(be);

        ms.translate(0.5, 0.5, 0.5);
        ms.mulPose(be.getBlockState().getValue(SignalGeneratorBlock.FACING).getRotation());


        Matrix4f m = ms.last().pose();
        Matrix3f n = ms.last().normal();

        float uvMin = (.5f + min);
        float uvMax = (.5f + max);

        float alpha = 1 /*Mth.clamp(be.remainingPolishAmount / UnpolishedComponentBlockEntity.DEFAULT_POLISHING_AMOUNT, 0f, 1f)*/;
        float stage = Math.round(1.5f);
        float stages = 25;

        float uvOffsetx = 0f;
        int offset = 1;
        float part = 1f / size;
        float totalpart = 1f / 16;

        light = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(be.getBlockState().getValue(SignalGeneratorBlock.FACING), 1));
        for (int i = 0; i < size; i++) {
            float x = (i * totalpart) + (1 / 16f) - min;
            float y = .5f;
            Signal[] signals = spaceArray(be.signals);
            if (signals != null && signals[i] != null)
                y = (float) (((signals[i].amplitude) / be.highestValue) / -2.1f+(Math.random()/64))+((be.lowestValue/be.highestValue))/4;
            buffer
                    .vertex(m, x, 0.5001f, min)
                    .color(1f, 1f, 1f, alpha)
                    .uv(uvMin + uvOffsetx, (uvMin / stages))
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(light)
                    .normal(n, 0, 1, 0)
                    .endVertex();

            buffer
                    .vertex(m, x, 0.5001f, y)
                    .color(1f, 1f, 1f, alpha)
                    .uv(uvMin + uvOffsetx, (uvMax / stages))
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(light)
                    .normal(n, 0, 1, 0)
                    .endVertex();

            buffer
                    .vertex(m, x + part, 0.5001f, y)
                    .color(1f, 1f, 1f, alpha)
                    .uv(uvMax + uvOffsetx, (uvMax / stages))
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(light)
                    .normal(n, 0, 1, 0)
                    .endVertex();

            buffer
                    .vertex(m, x + part, 0.5001f, min)
                    .color(1f, 1f, 1f, alpha)
                    .uv(uvMax + uvOffsetx, (uvMin / stages))
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(light)
                    .normal(n, 0, 1, 0)
                    .endVertex();
        }

    }

    private static boolean[] findIndexes(int n, int r) {
        boolean[] arrayWithObjects = new boolean[n];
        if (r<2) {
            arrayWithObjects[7] = true;
            return arrayWithObjects;
        }

        int quotient = (n - 1) / (r - 1);
        int remainder = (n - 1) % (r - 1);

        int index = 0;
        do {
            arrayWithObjects[index] = true;
        } while ((index += quotient + (remainder-- > 0 ? 1 : 0)) < n);

        return arrayWithObjects;
    }

    public Signal[] spaceArray(Signal[] signals) {
        Signal[] signals1 = new Signal[size];
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (signals[i] != null)
                count++;
        }
        boolean[] booleans = findIndexes(size, count);
        int i = 0;
        for (int x = 0; x < size; x++) {
            if (booleans[x]) {
                signals1[x] = signals[i];
                i++;
            }
        }
        return signals1;
    }

    private float getMaxPlaneExtent(SignalReadoutBlockEntity be) {
        return -(0.5f);
    }

    private float getMinPlaneExtent(SignalReadoutBlockEntity be) {
        return 0.5f;
    }

    public boolean isInvalid(SignalReadoutBlockEntity be) {
        return !be.hasLevel() || be.getBlockState()
                .getBlock() == Blocks.AIR;
    }

    @Override
    public boolean shouldRender(SignalReadoutBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return (BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos) && (pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos().relative(pBlockEntity.getBlockState().getValue(SignalGeneratorBlock.FACING), 1)).is(Blocks.AIR) || !pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos().relative(pBlockEntity.getBlockState().getValue(SignalGeneratorBlock.FACING), 1)).canOcclude()));
    }
}