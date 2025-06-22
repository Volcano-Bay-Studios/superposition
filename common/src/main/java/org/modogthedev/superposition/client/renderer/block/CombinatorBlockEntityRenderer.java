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
import org.modogthedev.superposition.blockentity.CombinatorBlockEntity;

import java.util.Objects;

public class CombinatorBlockEntityRenderer implements BlockEntityRenderer<CombinatorBlockEntity> {

    public Font font;

    public CombinatorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
    }

    @Override
    public void render(CombinatorBlockEntity be, float pPartialTick, PoseStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        if (isInvalid(be))
            return;

        float min = getMinPlaneExtent(be);
        float max = getMaxPlaneExtent(be);

        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);
        ms.mulPose(be.getBlockState().getValue(SignalGeneratorBlock.FACING).getRotation());

        String text = "";
        if (be.getMode() != null)
            text = be.getMode().getDisplayText();
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

    private float getMaxPlaneExtent(CombinatorBlockEntity be) {
        return -(0.5f);
    }

    private float getMinPlaneExtent(CombinatorBlockEntity be) {
        return 0.5f;
    }

    public boolean isInvalid(CombinatorBlockEntity be) {
        return !be.hasLevel() || be.getBlockState()
                .getBlock() == Blocks.AIR;
    }

    @Override
    public boolean shouldRender(CombinatorBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return (BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos) && (pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos().relative(pBlockEntity.getBlockState().getValue(SignalGeneratorBlock.FACING), 1)).is(Blocks.AIR) || !pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos().relative(pBlockEntity.getBlockState().getValue(SignalGeneratorBlock.FACING), 1)).canOcclude()));
    }
}
