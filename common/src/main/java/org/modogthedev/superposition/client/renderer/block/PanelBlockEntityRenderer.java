package org.modogthedev.superposition.client.renderer.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import foundry.veil.api.client.render.MatrixStack;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3f;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.core.*;
import org.modogthedev.superposition.item.WidgetItem;
import org.modogthedev.superposition.system.widget.Widget;
import org.modogthedev.superposition.system.widget.WidgetRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.modogthedev.superposition.util.SignalActorTickingBlock.FACING;

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

        List<Widget> widgets = new ArrayList<>(be.getWidgets());
        ms.translate(0, 9.001 / 16f, 0);
        for (Widget widget : widgets) {
            WidgetRenderer<Widget> widgetRenderer = Widget.getRenderer(widget);
            if (widgetRenderer != null) {
                ms.matrixPush();
                Vector2ic position = widget.getPosition();
                ms.translate(position.x() / 16f, 0, position.y() / 16f);
                widgetRenderer.render(widget, state, pPartialTick, ms.toPoseStack(), bufferSource, light, pPackedOverlay,  new Color(1f,1f,1f,1f));
                ms.matrixPop();
            }
        }



        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (mc.hitResult instanceof BlockHitResult blockHitResult && be.equals(player.level().getBlockEntity(blockHitResult.getBlockPos()))) {
            ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!itemInHand.is(SuperpositionItems.WIDGET.get())) {
                itemInHand = player.getItemInHand(InteractionHand.OFF_HAND);
            }
            if (itemInHand.is(SuperpositionItems.WIDGET.get())) {

                ResourceLocation type = WidgetItem.getType(itemInHand);
                Widget widget = SuperpositionWidgets.WIDGET.asVanillaRegistry().get(type);
                if (widget != null) {
                    WidgetRenderer<Widget> widgetRenderer = Widget.getRenderer(widget);
                    if (widgetRenderer != null) {
                        ms.matrixPush();
                        Vector3f pos = new Vector3f(blockHitResult.getLocation().toVector3f());
                        Vector3f position = be.transformLocal(pos);
                        Vector2i target = WidgetItem.target;
                        target.set((int) (position.x * 16 - widget.getBounds().x * 8), (int) (position.z * 16 - widget.getBounds().z * 8));
                        target.set((int) Mth.clamp(target.x,hasRight ? -16 : 0,(hasLeft ? 32 : 16) - widget.getBounds().x * 16), (int) Mth.clamp(target.y,0,16 - widget.getBounds().z * 16));

                        ms.translate(target.x() / 16f, 1/2048f, target.y() / 16f);

                        Vector2i min = new Vector2i();
                        Vector2i max = new Vector2i();


                        min.set(target.x,target.y);
                        max.set(min);
                        max.add((int) (widget.getBounds().x * 16), (int) (widget.getBounds().z * 16));

                        Vector2i otherMin = new Vector2i();
                        Vector2i otherMax = new Vector2i();
                        boolean collide = false;

                        for (Widget otherWidget : widgets) {
                            otherMin.set(otherWidget.getPosition().x,otherWidget.getPosition().y);
                            otherMax.set(otherMin);
                            otherMax.add((int) (otherWidget.getBounds().x * 16), (int) (otherWidget.getBounds().z * 16));

                            if (boxesOverlap(min,max,otherMin,otherMax)) {
                                collide = true;
                                break;
                            }
                        }
                        if (hasRight && be.getLevel().getBlockEntity(be.getBlockPos().relative(dir.getCounterClockWise())) instanceof PanelBlockEntity panel) {
                            for (Widget otherWidget : panel.getWidgets()) {
                                otherMin.set(otherWidget.getPosition().x - 16,otherWidget.getPosition().y);
                                otherMax.set(otherMin);
                                otherMax.add((int) (otherWidget.getBounds().x * 16), (int) (otherWidget.getBounds().z * 16));

                                if (boxesOverlap(min,max,otherMin,otherMax)) {
                                    collide = true;
                                    break;
                                }
                            }
                        }

                        if (hasLeft && be.getLevel().getBlockEntity(be.getBlockPos().relative(dir.getClockWise())) instanceof PanelBlockEntity panel) {
                            for (Widget otherWidget : panel.getWidgets()) {
                                otherMin.set(otherWidget.getPosition().x + 16,otherWidget.getPosition().y);
                                otherMax.set(otherMin);
                                otherMax.add((int) (otherWidget.getBounds().x * 16), (int) (otherWidget.getBounds().z * 16));

                                if (boxesOverlap(min,max,otherMin,otherMax)) {
                                    collide = true;
                                    break;
                                }
                            }
                        }

                        Color color;
                        if (collide) {
                            color = new Color(1f, 0.5f, 0.5f, 0.5f);
                            WidgetItem.fail = true;
                        } else {
                            color = new Color(0.35f, 1f, 0.6f, 0.5f);
                            WidgetItem.fail = false;
                        }
                        widgetRenderer.render(widget, state, pPartialTick, ms.toPoseStack(), bufferSource, light, pPackedOverlay, color);

                        ms.matrixPop();
                    }
                }
            }
        }
        ms.matrixPop();
    }

    public boolean boxesOverlap(Vector2i b1Min, Vector2i b1Max, Vector2i b2Min, Vector2i b2Max) {
        return b1Min.x < b2Max.x && b1Max.x > b2Min.x &&
                b1Min.y < b2Max.y && b1Max.y > b2Min.y;
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
