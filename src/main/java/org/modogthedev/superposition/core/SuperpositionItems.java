package org.modogthedev.superposition.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.item.ScrewdriverItem;


public class SuperpositionItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Superposition.MODID);
    public static final RegistryObject<ScrewdriverItem> SCREWDRIVER = ITEMS.register("screwdriver",() -> new ScrewdriverItem(new Item.Properties().stacksTo(1)));
}
