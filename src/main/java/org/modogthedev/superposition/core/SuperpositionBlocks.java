package org.modogthedev.superposition.core;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.*;

import java.util.function.Supplier;

public class SuperpositionBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Superposition.MODID);

    public static final RegistryObject<SignalGeneratorBlock> SIGNAL_GENERATOR = registerBlock("signal_generator",
            () -> new SignalGeneratorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistryObject<ModulatorBlock> MODULATOR = registerBlock("modulator",
            () -> new ModulatorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f)
            ));
    public static final RegistryObject<AntennaBlock> ANTENNA = registerBlock("antenna",
            () -> new AntennaBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)
                    .strength(2.0f, 8f).noOcclusion().lightLevel(value -> AntennaBlock.isCap(value) ? 4 : 0)
            ));
    public static final RegistryObject<AmplifierBlock> AMPLIFIER = registerBlock("amplifier",
            () -> new AmplifierBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)
                    .strength(2.0f, 8f)
            ));
    public static final RegistryObject<ReceiverBlock> RECEIVER = registerBlock("receiver",
            () -> new ReceiverBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f)
            ));
    public static final RegistryObject<FilterBlock> FILTER = registerBlock("filter",
            () -> new FilterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f)
            ));
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return SuperpositionItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
