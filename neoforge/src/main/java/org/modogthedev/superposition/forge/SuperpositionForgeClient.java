package org.modogthedev.superposition.forge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.SuperpositionClient;
import org.modogthedev.superposition.client.renderer.ui.SuperpositionUITooltipRenderer;

@Mod(value = Superposition.MODID, dist = Dist.CLIENT)
public class SuperpositionForgeClient {

    public SuperpositionForgeClient(IEventBus modEventBus) {
        SuperpositionClient.init();
        modEventBus.addListener(this::registerBlockEntityRenderers);
        modEventBus.addListener(this::registerGuiOverlays);
    }

    private void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Superposition.id("uitooltip"), SuperpositionUITooltipRenderer::renderOverlay);
    }

    private void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        SuperpositionClient.registerBlockEntityRenderers(event::registerBlockEntityRenderer);
    }
}
