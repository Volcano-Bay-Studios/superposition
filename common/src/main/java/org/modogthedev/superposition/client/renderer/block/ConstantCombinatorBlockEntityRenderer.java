package org.modogthedev.superposition.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.ConstantCombinatorBlockEntity;

import java.util.Objects;

public class ConstantCombinatorBlockEntityRenderer implements BlockEntityRenderer<ConstantCombinatorBlockEntity> {

    public Font font;

    public ConstantCombinatorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
    }

    @Override
    public void render(ConstantCombinatorBlockEntity be, float pPartialTick, PoseStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        if (isInvalid(be))
            return;

        float min = getMinPlaneExtent(be);
        float max = getMaxPlaneExtent(be);

        ms.translate(0.5, 0.5, 0.5);
        ms.mulPose(be.getBlockState().getValue(SignalGeneratorBlock.FACING).getRotation());

        String text = be.getOutputString().substring(0, Math.min(3, be.getOutputString().length()));
        if (be.getOutputString().length() > 3)
            text = text + "-";

        if (!text.equals("")) {
            ms.pushPose();
            if (Objects.equals(text, "x")) {
                ms.translate(0, 0, -.02);
            }
            if (text.length() > 1) {
                ms.translate(0.022f, .5, -.35);
                ms.mulPose(new Quaternionf(0.10f, 0, 0, 0.10f));
            } else {
                ms.translate(0.022f, .5, -.66);
                ms.mulPose(new Quaternionf(0.15f, 0, 0, 0.15f));
            }


            this.font.drawInBatch(text, -this.font.width(text) / 2f, 9, 3979870, false, ms.last().pose(), bufferSource, Font.DisplayMode.POLYGON_OFFSET, 0, LightTexture.FULL_BRIGHT);

            ms.popPose();
        }
    }

    private float getMaxPlaneExtent(ConstantCombinatorBlockEntity be) {
        return -(0.5f);
    }

    private float getMinPlaneExtent(ConstantCombinatorBlockEntity be) {
        return 0.5f;
    }

    public boolean isInvalid(ConstantCombinatorBlockEntity be) {
        return !be.hasLevel() || be.getBlockState()
                .getBlock() == Blocks.AIR;
    }

    @Override
    public boolean shouldRender(ConstantCombinatorBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return (BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos) && (pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos().relative(pBlockEntity.getBlockState().getValue(SignalGeneratorBlock.FACING), 1)).is(Blocks.AIR) || !pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos().relative(pBlockEntity.getBlockState().getValue(SignalGeneratorBlock.FACING), 1)).canOcclude()));
    }
}
