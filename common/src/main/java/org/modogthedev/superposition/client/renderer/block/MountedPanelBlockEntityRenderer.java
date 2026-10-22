package org.modogthedev.superposition.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import org.modogthedev.superposition.blockentity.MountedPanelBlockEntity;
import org.modogthedev.superposition.client.renderer.WidgetSurfaceRenderer;

public class MountedPanelBlockEntityRenderer implements BlockEntityRenderer<MountedPanelBlockEntity> {
    public MountedPanelBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(MountedPanelBlockEntity be, float pPartialTick, PoseStack ps, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        if (!be.hasLevel() || be.getBlockState().getBlock() == Blocks.AIR) {
            return;
        }
        WidgetSurfaceRenderer.render(be, pPartialTick, (MatrixStack) ps, bufferSource, light, pPackedOverlay);
    }
}
