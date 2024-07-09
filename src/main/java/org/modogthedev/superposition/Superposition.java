package org.modogthedev.superposition;

import com.mojang.logging.LogUtils;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.color.ColorTheme;
import foundry.veil.api.client.color.theme.IThemeProperty;
import foundry.veil.forge.VeilForgeClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.superposition.core.*;
import org.modogthedev.superposition.event.ClientEvents;
import org.modogthedev.superposition.networking.Messages;
import org.modogthedev.superposition.system.signal.ClientSignalManager;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Superposition.MODID)
public class Superposition {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "superposition";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ColorTheme SUPERPOSITION_THEME = new ColorTheme();

    public Superposition() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> SuperpositionClient::init);
        // Register the commonSetup method for modloading
        innitTheme();
        modEventBus.addListener(this::commonSetup);
        SuperpositionItems.ITEMS.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);
        SuperpositionBlockEntity.BLOCK_ENTITIES.register(modEventBus);
        SuperpositionBlocks.BLOCKS.register(modEventBus);
        SuperpositionSounds.SOUND_EVENTS.register(modEventBus);
        Messages.register();

        modEventBus.addListener(this::addCreativeTab);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(SignalManager::tick); //TODO only add to the side its used on
        bus.addListener(ClientSignalManager::tick);
        bus.addListener(ClientEvents::clientTickEvent);
    }
    public void innitTheme() {
        SUPERPOSITION_THEME.addColor(new Color(50, 168, 82,200));
        SUPERPOSITION_THEME.addColor(new Color(60, 186, 94,255));
        SUPERPOSITION_THEME.addColor(new Color(44, 150, 72,255));
//        SUPERPOSITION_THEME.addColor("connectingLine",new Color(44, 150, 72));
//        SUPERPOSITION_THEME.addProperty("connectingLineThickness", new IThemeProperty<Float>() {
//            @Override
//            public String getName() {
//                return null;
//            }
//
//            @Override
//            public void setName(String s) {
//
//            }
//
//            @Override
//            public Float getValue() {
//                return 1f;
//            }
//
//            @Override
//            public Class<?> getType() {
//                return Float.class;
//            }
//        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }
    public void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeModeTab.TAB.get()) {
            event.accept(SuperpositionBlocks.SIGNAL_GENERATOR.get().asItem());
            for (RegistryObject<Item> object: SuperpositionItems.ITEMS.getEntries()) {
                event.accept(object.get());
            }
        }
    }


    public static ResourceLocation asResource(String loc) {
        return new ResourceLocation(MODID, loc);
    }
}
