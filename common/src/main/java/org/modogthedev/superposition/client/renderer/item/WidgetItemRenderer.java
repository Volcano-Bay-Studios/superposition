package org.modogthedev.superposition.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.modogthedev.superposition.core.SuperpositionWidgets;
import org.modogthedev.superposition.item.WidgetItem;
import org.modogthedev.superposition.system.widget.Widget;
import org.modogthedev.superposition.system.widget.WidgetRenderer;

import java.awt.*;

public class WidgetItemRenderer extends BlockEntityWithoutLevelRenderer {

    public WidgetItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ResourceLocation type = WidgetItem.getType(stack);
        float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        if (type != null) {
            Widget widget = SuperpositionWidgets.WIDGET.asVanillaRegistry().get(type);
            if (widget != null) {
                WidgetRenderer<Widget> widgetRenderer = Widget.getRenderer(widget);
                if (widgetRenderer != null) {
                    poseStack.pushPose();
                    poseStack.translate(widget.getBounds().x/2f - 2/16f,widget.getBounds().y/2f - 2/16f,widget.getBounds().z/2f - 2/16f);
                    widgetRenderer.render(widget, Blocks.AIR.defaultBlockState(),partialTicks,poseStack,buffer,packedLight,packedOverlay, new Color(1f,1f,1f,1f));
                    poseStack.popPose();
                }
            }
        }
    }
}
