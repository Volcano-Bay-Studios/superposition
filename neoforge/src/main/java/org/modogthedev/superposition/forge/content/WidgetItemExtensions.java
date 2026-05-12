package org.modogthedev.superposition.forge.content;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.modogthedev.superposition.client.renderer.item.WidgetItemRenderer;

public class WidgetItemExtensions implements IClientItemExtensions {
    private final WidgetItemRenderer WIDGET_ITEM_RENDERER = new WidgetItemRenderer();
    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return WIDGET_ITEM_RENDERER;
    }
}
