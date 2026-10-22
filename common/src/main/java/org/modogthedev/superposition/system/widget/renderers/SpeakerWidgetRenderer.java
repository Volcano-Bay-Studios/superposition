package org.modogthedev.superposition.system.widget.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.system.widget.WidgetRenderer;
import org.modogthedev.superposition.system.widget.widgets.SpeakerWidget;

import java.awt.*;
import java.util.Map;

public class SpeakerWidgetRenderer extends WidgetRenderer<SpeakerWidget> {
    public SpeakerWidgetRenderer(Map<String, PartialModel> modelMap) {
        super(modelMap);
    }

    @Override
    public void render(SpeakerWidget widget, BlockState state, float pPartialTick, PoseStack ps, MultiBufferSource bufferSource, int light, int pPackedOverlay, Color color) {
        super.render(widget, state, pPartialTick, ps, bufferSource, light, pPackedOverlay, color);
        PartialModel speaker = getModel("speaker");
        renderPartial(speaker, state, ps, bufferSource.getBuffer(RenderType.translucent()), light, widget.isPlaying() ? widget.getColor(color) : color);
    }
}
