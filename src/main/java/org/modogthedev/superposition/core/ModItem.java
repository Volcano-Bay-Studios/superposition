package org.modogthedev.superposition.core;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.superposition.Superposition;

import static org.modogthedev.superposition.core.ModCreativeModeTab.addToTab;

public class ModItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Superposition.MODID);

    public static final RegistryObject<BlockItem> SIGNAL_GENERATOR = addToTab(ITEMS.register("signal_generator",
            () -> new BlockItem(ModBlock.SIGNAL_GENERATOR.get(), new Item.Properties())));
    public static final RegistryObject<BlockItem> MODULATOR = addToTab(ITEMS.register("modulator",
            () -> new BlockItem(ModBlock.MODULATOR.get(), new Item.Properties())));
}
