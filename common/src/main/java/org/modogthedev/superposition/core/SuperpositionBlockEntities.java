package org.modogthedev.superposition.core;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.blockentity.*;

import java.util.function.Supplier;

public class SuperpositionBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Superposition.MODID, Registries.BLOCK_ENTITY_TYPE);
    public static final RegistrySupplier<BlockEntityType<SignalGeneratorBlockEntity>> SIGNAL_GENERATOR =
            registerBlockEntity("signal_generator",
                    () -> BlockEntityType.Builder.of(SignalGeneratorBlockEntity::new, SuperpositionBlocks.SIGNAL_GENERATOR.get()));
    public static final RegistrySupplier<BlockEntityType<AmplifierBlockEntity>> AMPLIFIER =
            registerBlockEntity("amplifier",
                    () -> BlockEntityType.Builder.of(AmplifierBlockEntity::new, SuperpositionBlocks.AMPLIFIER.get()));
    public static final RegistrySupplier<BlockEntityType<TransmitterBlockEntity>> TRANSMITTER =
            registerBlockEntity("transmitter",
                    () -> BlockEntityType.Builder.of(TransmitterBlockEntity::new, SuperpositionBlocks.TRANSMITTER.get()));
    public static final RegistrySupplier<BlockEntityType<ReceiverBlockEntity>> RECEIVER =
            registerBlockEntity("receiver",
                    () -> BlockEntityType.Builder.of(ReceiverBlockEntity::new, SuperpositionBlocks.RECEIVER.get()));
    public static final RegistrySupplier<BlockEntityType<FilterBlockEntity>> FILTER =
            registerBlockEntity("filter",
                    () -> BlockEntityType.Builder.of(FilterBlockEntity::new, SuperpositionBlocks.FILTER.get()));
    public static final RegistrySupplier<BlockEntityType<ComputerBlockEntity>> COMPUTER =
            registerBlockEntity("computer",
                    () -> BlockEntityType.Builder.of(ComputerBlockEntity::new, SuperpositionBlocks.COMPUTER.get()));
    public static final RegistrySupplier<BlockEntityType<MonitorBlockEntity>> SIGNAL_READOUT =
            registerBlockEntity("signal_readout",
                    () -> BlockEntityType.Builder.of(MonitorBlockEntity::new, SuperpositionBlocks.SIGNAL_READOUT.get()));
    public static final RegistrySupplier<BlockEntityType<AnalyserBlockEntity>> ANALYSER =
            registerBlockEntity("analyser",
                    () -> BlockEntityType.Builder.of(AnalyserBlockEntity::new, SuperpositionBlocks.ANALYSER.get()));

    // T extends Item, so the cast is fine. We're also registering T, so it's also fine there.
    private static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> registerBlockEntity(String name, Supplier<BlockEntityType.Builder<T>> blockEntity) {
        return BLOCK_ENTITIES.register(name, () -> blockEntity.get().build(null));
    }
}