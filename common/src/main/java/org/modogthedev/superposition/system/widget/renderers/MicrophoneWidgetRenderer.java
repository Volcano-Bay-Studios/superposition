package org.modogthedev.superposition.system.widget.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.system.widget.WidgetRenderer;
import org.modogthedev.superposition.system.widget.widgets.MicrophoneWidget;

import java.awt.*;
import java.util.Map;

public class MicrophoneWidgetRenderer extends WidgetRenderer<MicrophoneWidget> {
    public MicrophoneWidgetRenderer(Map<String, PartialModel> modelMap) {
        super(modelMap);
    }

    @Override
    public void render(MicrophoneWidget widget, BlockState state, float pPartialTick, PoseStack ps, MultiBufferSource bufferSource, int light, int pPackedOverlay, Color color) {
        super.render(widget, state, pPartialTick, ps, bufferSource, light, pPackedOverlay, color);
        PartialModel head = getModel("microphone_head");
        renderPartial(head, state, ps, bufferSource.getBuffer(RenderType.solid()), light, widget.isLive() ? widget.getColor(color) : color);
        PartialModel microphone = getModel("microphone");
        renderPartial(microphone, state, ps, bufferSource.getBuffer(RenderType.translucent()), light, color);
    }
}
