package org.modogthedev.superposition.core;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.Superposition;

public class SuperpositionTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Superposition.MODID, Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> TAB = TABS.register(Superposition.id("tab"),
            () -> CreativeTabRegistry.create(
                    Component.translatable("creativemodetab.superposition"),
                    () -> new ItemStack(SuperpositionBlocks.SIGNAL_GENERATOR.get().asItem())
            )
    );
}