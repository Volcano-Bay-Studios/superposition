package org.modogthedev.superposition.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.client.renderer.block.SignalGeneratorBlockEntityRenderer;
import org.modogthedev.superposition.core.ModBlockEntity;

@Mod.EventBusSubscriber(modid = Superposition.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntity.SIGNAL_GENERATOR.get(), SignalGeneratorBlockEntityRenderer::new);
    }
}
