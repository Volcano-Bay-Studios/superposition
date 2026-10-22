package org.modogthedev.superposition.client.renderer.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import foundry.veil.api.client.render.MatrixStack;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.client.renderer.WidgetSurfaceRenderer;
import org.modogthedev.superposition.core.SuperpositionBlocks;
import org.modogthedev.superposition.core.SuperpositionPartials;

import static org.modogthedev.superposition.util.block.SignalActorTickingBlock.FACING;

public class PanelBlockEntityRenderer implements BlockEntityRenderer<PanelBlockEntity> {
    public PanelBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(PanelBlockEntity be, float pPartialTick, PoseStack ps, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        if (isInvalid(be))
            return;
        BlockState state = be.getBlockState();
        Direction dir = state.getValue(FACING);
        BlockState leftState = be.getLevel().getBlockState(be.getBlockPos().relative(dir.getClockWise()));
        BlockState rightState = be.getLevel().getBlockState(be.getBlockPos().relative(dir.getCounterClockWise()));
        boolean hasLeft = leftState.is(SuperpositionBlocks.PANEL.get()) && leftState.getValue(FACING).equals(dir);
        boolean hasRight = rightState.is(SuperpositionBlocks.PANEL.get()) && rightState.getValue(FACING).equals(dir);

        RenderSystem.setShaderColor(1f,1f,1f,1f);

        MatrixStack ms = (MatrixStack) ps;

        ms.matrixPush();
        ms.translate(0.5f, 0, 0.5f);
        ms.rotate(Math.atan2(-dir.getStepX(), -dir.getStepZ()), 0, 1, 0);
        ms.translate(-0.5f, 0, -0.5f);


        ms.matrixPush();
        ms.translate(0, be.getFrontHeight() / 16f, 0);
        CachedBuffers.partial(SuperpositionPartials.PANEL_FRONT_LEGS, state)
                .light(light)
                .renderInto(ms.toPoseStack(), bufferSource.getBuffer(RenderType.solid()));
        ms.matrixPop();

        ms.matrixPush();
        ms.translate(0, be.getBackHeight() / 16f, 0);
        CachedBuffers.partial(SuperpositionPartials.PANEL_BACK_LEGS, state)
                .light(light)
                .renderInto(ms.toPoseStack(), bufferSource.getBuffer(RenderType.solid()));
        ms.matrixPop();
        ms.matrixPop();

        ms.matrixPush();
        ms.toPoseStack().mulPose(be.getPanelMatrix());

        PartialModel panelSurface = SuperpositionPartials.PANEL_SURFACE;
        if (hasLeft && hasRight) {
            panelSurface = SuperpositionPartials.PANEL_SURFACE_MIDDLE;
        } else if (hasLeft) {
            panelSurface = SuperpositionPartials.PANEL_SURFACE_LEFT;
        } else if (hasRight) {
            panelSurface = SuperpositionPartials.PANEL_SURFACE_RIGHT;
        }

        CachedBuffers.partial(panelSurface, state)
                .light(light)
                .renderInto(ms.toPoseStack(), bufferSource.getBuffer(RenderType.solid()));

        ms.matrixPop();

        WidgetSurfaceRenderer.render(be, pPartialTick, ms, bufferSource, light, pPackedOverlay);
    }

    public boolean isInvalid(PanelBlockEntity be) {
        return !be.hasLevel() || be.getBlockState()
                .getBlock() == Blocks.AIR;
    }

    @Override
    public boolean shouldRender(PanelBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return (BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos));
    }
}
