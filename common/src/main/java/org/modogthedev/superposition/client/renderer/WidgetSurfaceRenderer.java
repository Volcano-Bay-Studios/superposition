package org.modogthedev.superposition.client.renderer;

import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3d;
import org.modogthedev.superposition.blockentity.WidgetPanelBlockEntity;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.modogthedev.superposition.core.SuperpositionWidgets;
import org.modogthedev.superposition.item.WidgetItem;
import org.modogthedev.superposition.system.widget.Widget;
import org.modogthedev.superposition.system.widget.WidgetRenderer;
import org.modogthedev.superposition.system.widget.WidgetSurface;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Renders the widgets on a surface. This is shared by everything that holds widgets.
 */
public class WidgetSurfaceRenderer {

    /**
     * Renders the widgets of a panel, and the widget the player is about to place on it.
     */
    public static void render(WidgetPanelBlockEntity be, float pPartialTick, MatrixStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        WidgetSurface surface = be.getSurface();
        BlockState state = be.getBlockState();

        ms.matrixPush();
        ms.toPoseStack().mulPose(surface.transform());
        ms.translate(0, 0.001 / 16f, 0);
        renderWidgets(be.getWidgets(), state, pPartialTick, ms, bufferSource, light, pPackedOverlay);
        renderPlacement(be, surface, state, pPartialTick, ms, bufferSource, light, pPackedOverlay);
        ms.matrixPop();
    }

    /**
     * Renders widgets onto a surface that the matrix stack has already been transformed to.
     */
    public static void renderWidgets(Collection<Widget> widgets, BlockState state, float pPartialTick, MatrixStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        List<Widget> toRender = new ArrayList<>(widgets);
        for (Widget widget : toRender) {
            WidgetRenderer<Widget> widgetRenderer = Widget.getRenderer(widget);
            if (widgetRenderer != null) {
                ms.matrixPush();
                Vector2ic position = widget.getPosition();
                ms.translate(position.x() / 16f, 0, position.y() / 16f);
                widgetRenderer.render(widget, state, pPartialTick, ms.toPoseStack(), bufferSource, light, pPackedOverlay, new Color(1f, 1f, 1f, 1f));
                ms.matrixPop();
            }
        }
    }

    private static void renderPlacement(WidgetPanelBlockEntity be, WidgetSurface surface, BlockState state, float pPartialTick, MatrixStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !(mc.hitResult instanceof BlockHitResult blockHitResult) || !be.equals(player.level().getBlockEntity(blockHitResult.getBlockPos()))) {
            return;
        }
        ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!itemInHand.is(SuperpositionItems.WIDGET.get())) {
            itemInHand = player.getItemInHand(InteractionHand.OFF_HAND);
        }
        if (!itemInHand.is(SuperpositionItems.WIDGET.get())) {
            return;
        }

        ResourceLocation type = WidgetItem.getType(itemInHand);
        Widget widget = SuperpositionWidgets.WIDGET.asVanillaRegistry().get(type);
        if (widget == null) {
            return;
        }
        WidgetRenderer<Widget> widgetRenderer = Widget.getRenderer(widget);
        if (widgetRenderer == null) {
            return;
        }

        Vec3 location = blockHitResult.getLocation();
        Vector3d position = be.toSurface(new Vector3d(location.x, location.y, location.z));
        Vector2i target = WidgetItem.target;
        target.set((int) (position.x * 16 - widget.getBounds().x * 8), (int) (position.z * 16 - widget.getBounds().z * 8));
        target.set(surface.clamp(target, widget));

        Color color;
        if (be.getWidgetContainer().canPlace(widget, target, surface, be.getNeighbours())) {
            color = new Color(0.35f, 1f, 0.6f, 0.5f);
            WidgetItem.fail = false;
        } else {
            color = new Color(1f, 0.5f, 0.5f, 0.5f);
            WidgetItem.fail = true;
        }

        ms.matrixPush();
        ms.translate(target.x() / 16f, 1 / 2048f, target.y() / 16f);
        widgetRenderer.render(widget, state, pPartialTick, ms.toPoseStack(), bufferSource, light, pPackedOverlay, color);
        ms.matrixPop();
    }
}
