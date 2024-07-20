package org.modogthedev.superposition.core;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.item.FilterItem;
import org.modogthedev.superposition.item.ScrewdriverItem;
import org.modogthedev.superposition.item.block.SignalReadoutBlockItem;


public class SuperpositionItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Superposition.MODID);
    public static final RegistryObject<ScrewdriverItem> SCREWDRIVER = ITEMS.register("screwdriver",() -> new ScrewdriverItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<FilterItem> HIGH_PASS_FILTER = ITEMS.register("high_pass_filter", () -> new FilterItem(new FilterItem.Properties().type(FilterItem.FilterType.HIGH_PASS),new Item.Properties().stacksTo(1)));
    public static final RegistryObject<FilterItem> LOW_PASS_FILTER = ITEMS.register("low_pass_filter", () -> new FilterItem(new FilterItem.Properties().type(FilterItem.FilterType.LOW_PASS),new Item.Properties().stacksTo(1)));
    public static final RegistryObject<FilterItem> BAND_PASS_FILTER = ITEMS.register("band_pass_filter", () -> new FilterItem(new FilterItem.Properties().type(FilterItem.FilterType.BAND_PASS),new Item.Properties().stacksTo(1)));
    public static final RegistryObject<SignalReadoutBlockItem> SIGNAL_READOUT = ITEMS.register("signal_readout",() -> new SignalReadoutBlockItem(SuperpositionBlocks.SIGNAL_READOUT.get(),new Item.Properties()));
}
