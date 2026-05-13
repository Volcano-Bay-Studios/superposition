package org.modogthedev.superposition.system.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Map;

public class WidgetRenderer<T extends Widget> {
    private Map<String, PartialModel> models;
    public WidgetRenderer(Map<String, PartialModel> modelMap) {
        this.models = modelMap;
    }

    protected PartialModel getModel(String path) {
        return models.get(path);
    }

    public void render(T widget, @Nullable BlockState state, float pPartialTick, PoseStack ms, MultiBufferSource bufferSource, int light, int pPackedOverlay, Color color) {

    }

    public void renderPartial(PartialModel model, @Nullable BlockState state, PoseStack ps, VertexConsumer buffer, int light, Color color) {
        CachedBuffers.partial(model,state)
                .light(light)
                .color(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha())
                .renderInto(ps,buffer);
    }
}
