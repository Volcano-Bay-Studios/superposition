package org.modogthedev.superposition.forge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.SuperpositionClient;

@Mod(value = Superposition.MODID, dist = Dist.CLIENT)
public class SuperpositionForgeClient {
    public SuperpositionForgeClient(IEventBus modEventBus) {
        modEventBus.addListener(this::registerBlockEntityRenderers);
        SuperpositionClient.init();
    }
    private void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        SuperpositionClient.registerBlockEntityRenderers();
    }
}
