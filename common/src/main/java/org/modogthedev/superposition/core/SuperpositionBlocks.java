package org.modogthedev.superposition.core;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.*;

import java.util.function.Supplier;

public class SuperpositionBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Superposition.MODID, Registries.BLOCK);
    public static final RegistrySupplier<SignalGeneratorBlock> SIGNAL_GENERATOR = registerBlock("signal_generator",
            () -> new SignalGeneratorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistrySupplier<SignalReadoutBlock> SIGNAL_READOUT = BLOCKS.register("signal_readout",
            () -> new SignalReadoutBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistrySupplier<AmplifierBlock> AMPLIFIER = registerBlock("amplifier",
            () -> new AmplifierBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistrySupplier<AntennaBlock> ANTENNA = registerBlock("antenna",
            () -> new AntennaBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)
                    .strength(2.0f, 8f).noOcclusion().lightLevel(value -> AntennaBlock.isCap(value) ? 4 : 0)
            ));
    public static final RegistrySupplier<TransmitterBlock> TRANSMITTER = registerBlock("transmitter",
            () -> new TransmitterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistrySupplier<ReceiverBlock> RECEIVER = registerBlock("receiver",
            () -> new ReceiverBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistrySupplier<FilterBlock> FILTER = registerBlock("filter",
            () -> new FilterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistrySupplier<CasingBlock> CASING = registerBlock("casing",
            () -> new CasingBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));

    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block) {
        RegistrySupplier<T> toReturn = BLOCKS.register(name, block);
        SuperpositionItems.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties().arch$tab(SuperpositionTabs.TAB)));
        return toReturn;
    }
}
