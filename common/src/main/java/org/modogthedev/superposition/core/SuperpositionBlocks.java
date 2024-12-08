package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
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

    public static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(Registries.BLOCK, Superposition.MODID);
    public static final RegistryObject<AntennaBlock> ANTENNA = registerBlock("antenna",
            () -> new AntennaBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS)
                    .strength(2.0f, 8f).noOcclusion().lightLevel(value -> AntennaBlock.isCap(value) ? 4 : 0)
            ));
    public static final RegistryObject<SignalGeneratorBlock> SIGNAL_GENERATOR = registerBlock("signal_generator",
            () -> new SignalGeneratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistryObject<MonitorBlock> SIGNAL_READOUT = registerBlock("signal_readout",
            () -> new MonitorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistryObject<TransmitterBlock> TRANSMITTER = registerBlock("transmitter",
            () -> new TransmitterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistryObject<ReceiverBlock> RECEIVER = registerBlock("receiver",
            () -> new ReceiverBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistryObject<AmplifierBlock> AMPLIFIER = registerBlock("amplifier",
            () -> new AmplifierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));

    public static final RegistryObject<FilterBlock> FILTER = registerBlock("filter",
            () -> new FilterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistryObject<ComputerBlock> COMPUTER = registerBlock("computer",
            () -> new ComputerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistryObject<AnalyserBlock> ANALYSER = registerBlock("analyser",
            () -> new AnalyserBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistryObject<CombinatorBlock> COMBINATOR = registerBlock("combinator",
            () -> new CombinatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));
    public static final RegistryObject<CasingBlock> CASING = registerBlock("casing",
            () -> new CasingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(2.0f, 8f).noOcclusion()
            ));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        SuperpositionItems.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    public static void bootstrap(){
    }
}
