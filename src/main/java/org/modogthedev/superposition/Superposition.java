package org.modogthedev.superposition;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.superposition.core.*;
import org.modogthedev.superposition.event.ClientEvents;
import org.modogthedev.superposition.networking.Messages;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Superposition.MODID)
public class Superposition {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "superposition";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public Superposition() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        SuperpositionItems.ITEMS.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);
        ModBlockEntity.BLOCK_ENTITIES.register(modEventBus);
        ModBlock.BLOCKS.register(modEventBus);
        SuperpositionSounds.SOUND_EVENTS.register(modEventBus);
        Messages.register();

        modEventBus.addListener(this::addCreativeTab);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(SignalManager::tick);
        bus.addListener(ClientEvents::clientTickEvent);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }
    public void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeModeTab.TAB.get()) {
            event.accept(ModBlock.SIGNAL_GENERATOR.get().asItem());
            for (RegistryObject<Item> object: SuperpositionItems.ITEMS.getEntries()) {
                event.accept(object.get());
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }
}
