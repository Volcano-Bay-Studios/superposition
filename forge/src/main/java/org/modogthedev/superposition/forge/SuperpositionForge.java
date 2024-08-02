package org.modogthedev.superposition.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.SuperpositionClient;

@Mod(Superposition.MODID)
public class SuperpositionForge {
    public SuperpositionForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus("superposition", modEventBus);
        modEventBus.addListener(this::registerBlockEntityRenderers);
        Superposition.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> SuperpositionClient::init);
    }
    private void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        SuperpositionClient.registerBlockEntityRenderers();
    }
}
