package org.modogthedev.superposition.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.superposition.Superposition;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Superposition.MODID);


    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativemodetab.superposition"))
                    .icon(SuperpositionBlocks.SIGNAL_GENERATOR.get().asItem()::getDefaultInstance)
                    .build()
    );

    public static void  register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}