package org.modogthedev.superposition.system.widget.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.system.widget.WidgetRenderer;
import org.modogthedev.superposition.system.widget.widgets.GaugeWidget;

import java.util.Map;

public class GaugeRenderer extends WidgetRenderer<GaugeWidget> {
    public GaugeRenderer(Map<String, PartialModel> modelMap) {
        super(modelMap);
    }

    @Override
    public void render(GaugeWidget widget, BlockState state, float pPartialTick, PoseStack ps, MultiBufferSource bufferSource, int light, int pPackedOverlay) {
        super.render(widget, state, pPartialTick, ps, bufferSource, light, pPackedOverlay);
        MatrixStack ms = (MatrixStack) ps;
        ms.matrixPush();
        ms.translate(1.5/16f,0/16f,1.5/16f);
        ms.rotate(Math.toRadians(widget.getRenderNeedlePosition(pPartialTick) * 90f),0,1,0);
        ms.translate(-0.5/16f,0/16f,-0.5/16f);

        PartialModel gaugeNeedle = getModel("gauge_needle");
        renderPartial(gaugeNeedle,state, ms.toPoseStack(),bufferSource.getBuffer(RenderType.solid()), light);
        ms.matrixPop();
        PartialModel gauge = getModel("gauge");
        renderPartial(gauge,state, ms.toPoseStack(),bufferSource.getBuffer(RenderType.translucent()), light);
    }
}
