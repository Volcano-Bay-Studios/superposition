package org.modogthedev.superposition.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.MonitorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.core.SuperpositionRenderTypes;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SuperpositionMth;


public class MonitorBlockEntityRenderer implements BlockEntityRenderer<MonitorBlockEntity> {

    public Font font;

    public MonitorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
    }

    static final int size = 12;

    @Override
    public void render(MonitorBlockEntity be, float pPartialTick, PoseStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        if (isInvalid(be))
            return;

        float min = getMinPlaneExtent(be);
        float max = getMaxPlaneExtent(be);

        ms.translate(0.5, 0.748, 0.5);
        ms.mulPose(be.getBlockState().getValue(SignalGeneratorBlock.FACING).getRotation());
        ms.translate(0, -.125, 0);


        Matrix4f m = ms.last().pose();
        Matrix3f n = ms.last().normal();

        float uvMin = (.5f + min);
        float uvMax = (.5f + max);

        float alpha = 1 /*Mth.clamp(be.remainingPolishAmount / UnpolishedComponentBlockEntity.DEFAULT_POLISHING_AMOUNT, 0f, 1f)*/;
        float stage = Math.round(1.5f);
        float stages = 25;

        float transformDown = Math.max(0, (-be.transformState / 10f) + 1);
        float transformUp = Mth.clamp(((be.transformState - 10) / 10f), 0, 1);
        float uvOffsetx = 0f;
        int offset = 2;
        float part = 1f / (size + 4);
        float totalPart = 1f / (size + 4);
        ms.pushPose();
        ms.translate(-.44f, .5, -.1);
        ms.mulPose(new Quaternionf(0.07f, 0, 0, 0.07f));
        Matrix4f textPose = ms.last().pose();

        int j = 0;
        for (String text : be.text) { //TODO: Finish text system
            float x = 1;
            this.font.drawInBatch(text.substring(0, (int) (text.length() * transformUp)), x, j * 9, 3979870, false, textPose, bufferSource, Font.DisplayMode.POLYGON_OFFSET, 0, LightTexture.FULL_BRIGHT);
            j++;
        }

        ms.popPose();
        light = LightTexture.FULL_BRIGHT;
        VertexConsumer buffer = null;
        if (SuperpositionConstants.bloomEnabled) {
            buffer = bufferSource.getBuffer(SuperpositionRenderTypes.bloomBlockPolygonOffset(Superposition.id("textures/screen/pixel.png")));
        } else {
            buffer = bufferSource.getBuffer(SuperpositionRenderTypes.blockPolygonOffset(Superposition.id("textures/screen/pixel.png")));
        }
        for (int i = 0; i < size; i++) {
            float x = (i * totalPart) + (offset / (size + 4f)) - min;
            float y = .21f;
            float yinverse;
            Signal[] signals = SuperpositionMth.spaceArray(be.signals, size);
            if (signals != null && signals[i] != null) {
                y = Math.max(-.061f, (float) ((((signals[i].getAmplitude()) / be.highestValue) / -6f) + ((be.lowestValue / be.highestValue) / 4))) * transformDown;
                y = Mth.lerp(-(transformDown - 1), y, .21f);
            }
            y += (float) (Math.random() / 64) * transformDown;
            yinverse = -y + .42f + (.05f * transformDown);

            buffer
                    .addVertex(m, x + part, 0.5001f, yinverse)
                    .setColor(1f, 1f, 1f, alpha)
                    .setUv(uvMax + uvOffsetx, (uvMin / stages))
                    .setLight(light)
                    .setNormal(ms.last(), 0, 1, 0);

            buffer
                    .addVertex(m, x + part, 0.5001f, y)
                    .setColor(1f, 1f, 1f, alpha)
                    .setUv(uvMax + uvOffsetx, (uvMin / stages))
                    .setLight(light)
                    .setNormal(ms.last(), 0, 1, 0);

            buffer
                    .addVertex(m, x, 0.5001f, y)
                    .setColor(1f, 1f, 1f, alpha)
                    .setUv(uvMin + uvOffsetx, (uvMax / stages))
                    .setLight(light)
                    .setNormal(ms.last(), 0, 1, 0);

            buffer
                    .addVertex(m, x, 0.5001f, yinverse)
                    .setColor(1f, 1f, 1f, alpha)
                    .setUv(uvMin + uvOffsetx, (uvMin / stages))
                    .setLight(light)
                    .setNormal(ms.last(), 0, 1, 0);


        }
//        ms.translate(0,-.00025,.22);
//        alpha = .4f;
//        VertexConsumer screenBuffer = bufferSource.getBuffer(SuperpositionRenderTypes.entityTranslucentEmissive(Superposition.id("textures/screen/monitor_screen.png")));
//        screenBuffer
//                .vertex(m, -0.375f, 0.5001f, -0.28125f)
//                .color(1f, 1f, 1f, alpha)
//                .uv(0,0)
//                .overlayCoords(OverlayTexture.NO_OVERLAY)
//                .uv2(light)
//                .normal(n, 0, 1, 0)
//                .endVertex();
//
//        screenBuffer
//                .vertex(m, -0.375f, 0.5001f, 0.28125f)
//                .color(1f, 1f, 1f, alpha)
//                .uv(0, 1)
//                .overlayCoords(OverlayTexture.NO_OVERLAY)
//                .uv2(light)
//                .normal(n, 0, 1, 0)
//                .endVertex();
//
//        screenBuffer
//                .vertex(m, 0.375f, 0.5001f, 0.28125f)
//                .color(1f, 1f, 1f, alpha)
//                .uv(1, 1)
//                .overlayCoords(OverlayTexture.NO_OVERLAY)
//                .uv2(light)
//                .normal(n, 0, 1, 0)
//                .endVertex();
//
//        screenBuffer
//                .vertex(m, 0.375f, 0.5001f, -0.28125f)
//                .color(1f, 1f, 1f, alpha)
//                .uv(1,0)
//                .overlayCoords(OverlayTexture.NO_OVERLAY)
//                .uv2(light)
//                .normal(n, 0, 1, 0)
//                .endVertex();
    }


    private float getMaxPlaneExtent(MonitorBlockEntity be) {
        return -(0.5f);
    }

    private float getMinPlaneExtent(MonitorBlockEntity be) {
        return 0.5f;
    }

    public boolean isInvalid(MonitorBlockEntity be) {
        return !be.hasLevel() || be.getBlockState()
                .getBlock() == Blocks.AIR;
    }

    @Override
    public boolean shouldRender(MonitorBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return (BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos));
    }
}