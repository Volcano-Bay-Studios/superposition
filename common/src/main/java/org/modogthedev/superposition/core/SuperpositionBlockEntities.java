package org.modogthedev.superposition.core;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.blockentity.*;

public class SuperpositionBlockEntities {
    public static final Holder.Reference<BlockEntityType<SignalGeneratorBlockEntity>> SIGNAL_GENERATOR =
            registerBlockEntity("signal_generator",
                    BlockEntityType.Builder.of(SignalGeneratorBlockEntity::new, SuperpositionBlocks.SIGNAL_GENERATOR.get())
                            .build(null));
    public static final Holder.Reference<BlockEntityType<AmplifierBlockEntity>> AMPLIFIER =
            registerBlockEntity("amplifier",
                    BlockEntityType.Builder.of(AmplifierBlockEntity::new, SuperpositionBlocks.AMPLIFIER.get())
                            .build(null));
    public static final Holder.Reference<BlockEntityType<TransmitterBlockEntity>> TRANSMITTER =
            registerBlockEntity("transmitter",
                    BlockEntityType.Builder.of(TransmitterBlockEntity::new, SuperpositionBlocks.TRANSMITTER.get())
                            .build(null));
    public static final Holder.Reference<BlockEntityType<ReceiverBlockEntity>> RECEIVER =
            registerBlockEntity("receiver",
                    BlockEntityType.Builder.of(ReceiverBlockEntity::new, SuperpositionBlocks.RECEIVER.get())
                            .build(null));
    public static final Holder.Reference<BlockEntityType<FilterBlockEntity>> FILTER =
            registerBlockEntity("filter",
                    BlockEntityType.Builder.of(FilterBlockEntity::new, SuperpositionBlocks.FILTER.get())
                            .build(null));
    public static final Holder.Reference<BlockEntityType<SignalReadoutBlockEntity>> SIGNAL_READOUT =
            registerBlockEntity("signal_readout",
                    BlockEntityType.Builder.of(SignalReadoutBlockEntity::new, SuperpositionBlocks.SIGNAL_READOUT.get())
                            .build(null));

    // T extends Item, so the cast is fine. We're also registering T, so it's also fine there.
    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> Holder.Reference<BlockEntityType<T>> registerBlockEntity(String name, BlockEntityType<T> blockEntity) {
        return (Holder.Reference<BlockEntityType<T>>) (Object) Registry.registerForHolder(BuiltInRegistries.BLOCK_ENTITY_TYPE, Superposition.id(name), blockEntity);
    }
}