package org.modogthedev.superposition.system.widget.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.system.widget.WidgetRenderer;
import org.modogthedev.superposition.system.widget.widgets.ButtonWidget;

import java.awt.*;
import java.util.Map;

public class ButtonWidgetRenderer extends WidgetRenderer<ButtonWidget> {
    public ButtonWidgetRenderer(Map<String, PartialModel> modelMap) {
        super(modelMap);
    }

    @Override
    public void render(ButtonWidget widget, BlockState state, float pPartialTick, PoseStack ps, MultiBufferSource bufferSource, int light, int pPackedOverlay, Color color) {
        super.render(widget, state, pPartialTick, ps, bufferSource, light, pPackedOverlay, color);
        MatrixStack ms = (MatrixStack) ps;
        ms.matrixPush();
        ms.translate(0, Mth.map(widget.getPosition(pPartialTick),0,1,-1/64f,(-1/16f)+(1/64f)),0);
        PartialModel gaugeNeedle = getModel("button_button");
        renderPartial(gaugeNeedle,state, ms.toPoseStack(),bufferSource.getBuffer(RenderType.solid()), light, widget.getColor(color));
        ms.matrixPop();
        PartialModel gauge = getModel("button");
        renderPartial(gauge,state, ms.toPoseStack(),bufferSource.getBuffer(RenderType.translucent()), light, color);
    }
}
