package org.modogthedev.superposition.core;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.blockentity.*;

public class SuperpositionBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Superposition.MODID);

    public static final RegistryObject<BlockEntityType<SignalGeneratorBlockEntity>> SIGNAL_GENERATOR =
            BLOCK_ENTITIES.register("signal_generator",
                    () -> BlockEntityType.Builder.of(SignalGeneratorBlockEntity::new, SuperpositionBlocks.SIGNAL_GENERATOR.get())
                            .build(null));
    public static final RegistryObject<BlockEntityType<AmplifierBlockEntity>> AMPLIFIER =
            BLOCK_ENTITIES.register("amplifier",
                    () -> BlockEntityType.Builder.of(AmplifierBlockEntity::new, SuperpositionBlocks.AMPLIFIER.get())
                            .build(null));
    public static final RegistryObject<BlockEntityType<TransmitterBlockEntity>> TRANSMITTER =
            BLOCK_ENTITIES.register("transmitter",
                    () -> BlockEntityType.Builder.of(TransmitterBlockEntity::new, SuperpositionBlocks.TRANSMITTER.get())
                            .build(null));
    public static final RegistryObject<BlockEntityType<ReceiverBlockEntity>> RECEIVER =
            BLOCK_ENTITIES.register("receiver",
                    () -> BlockEntityType.Builder.of(ReceiverBlockEntity::new, SuperpositionBlocks.RECEIVER.get())
                            .build(null));
    public static final RegistryObject<BlockEntityType<FilterBlockEntity>> FILTER =
            BLOCK_ENTITIES.register("filter",
                    () -> BlockEntityType.Builder.of(FilterBlockEntity::new, SuperpositionBlocks.FILTER.get())
                            .build(null));
    public static final RegistryObject<BlockEntityType<SignalReadoutBlockEntity>> SIGNAL_READOUT =
            BLOCK_ENTITIES.register("signal_readout",
                    () -> BlockEntityType.Builder.of(SignalReadoutBlockEntity::new, SuperpositionBlocks.SIGNAL_READOUT.get())
                            .build(null));
}