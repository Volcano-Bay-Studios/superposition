package org.modogthedev.superposition.core;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.item.FilterItem;
import org.modogthedev.superposition.item.ScrewdriverItem;
import org.modogthedev.superposition.item.block.SignalReadoutBlockItem;

import java.util.function.Supplier;

public class SuperpositionItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Superposition.MODID, Registries.ITEM);
    public static final RegistrySupplier<ScrewdriverItem> SCREWDRIVER = registerItem("screwdriver", () -> new ScrewdriverItem(new Item.Properties().stacksTo(1).arch$tab(SuperpositionTabs.TAB)));
    public static final RegistrySupplier<FilterItem> HIGH_PASS_FILTER = registerItem("high_pass_filter", () -> new FilterItem(new FilterItem.Properties().type(SuperpositionFilters.HIGH_PASS.get()), new Item.Properties().stacksTo(1).arch$tab(SuperpositionTabs.TAB)));
    public static final RegistrySupplier<FilterItem> LOW_PASS_FILTER = registerItem("low_pass_filter", () -> new FilterItem(new FilterItem.Properties().type(SuperpositionFilters.LOW_PASS.get()), new Item.Properties().stacksTo(1).arch$tab(SuperpositionTabs.TAB)));
    public static final RegistrySupplier<FilterItem> BAND_PASS_FILTER = registerItem("band_pass_filter", () -> new FilterItem(new FilterItem.Properties().type(SuperpositionFilters.BAND_PASS.get()), new Item.Properties().stacksTo(1).arch$tab(SuperpositionTabs.TAB)));
    public static final RegistrySupplier<SignalReadoutBlockItem> SIGNAL_READOUT = registerItem("signal_readout", () -> new SignalReadoutBlockItem(SuperpositionBlocks.SIGNAL_READOUT.get(), new Item.Properties().arch$tab(SuperpositionTabs.TAB)));

    private static <T extends Item> RegistrySupplier<T> registerItem(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
