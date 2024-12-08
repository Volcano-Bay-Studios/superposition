package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.blockentity.*;

import java.util.function.Supplier;

public class SuperpositionBlockEntities {

    public static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistrationProvider.get(Registries.BLOCK_ENTITY_TYPE, Superposition.MODID);
    public static final RegistryObject<BlockEntityType<SignalGeneratorBlockEntity>> SIGNAL_GENERATOR =
            registerBlockEntity("signal_generator",
                    () -> BlockEntityType.Builder.of(SignalGeneratorBlockEntity::new, SuperpositionBlocks.SIGNAL_GENERATOR.get()));
    public static final RegistryObject<BlockEntityType<AmplifierBlockEntity>> AMPLIFIER =
            registerBlockEntity("amplifier",
                    () -> BlockEntityType.Builder.of(AmplifierBlockEntity::new, SuperpositionBlocks.AMPLIFIER.get()));
    public static final RegistryObject<BlockEntityType<TransmitterBlockEntity>> TRANSMITTER =
            registerBlockEntity("transmitter",
                    () -> BlockEntityType.Builder.of(TransmitterBlockEntity::new, SuperpositionBlocks.TRANSMITTER.get()));
    public static final RegistryObject<BlockEntityType<ReceiverBlockEntity>> RECEIVER =
            registerBlockEntity("receiver",
                    () -> BlockEntityType.Builder.of(ReceiverBlockEntity::new, SuperpositionBlocks.RECEIVER.get()));
    public static final RegistryObject<BlockEntityType<FilterBlockEntity>> FILTER =
            registerBlockEntity("filter",
                    () -> BlockEntityType.Builder.of(FilterBlockEntity::new, SuperpositionBlocks.FILTER.get()));
    public static final RegistryObject<BlockEntityType<ComputerBlockEntity>> COMPUTER =
            registerBlockEntity("computer",
                    () -> BlockEntityType.Builder.of(ComputerBlockEntity::new, SuperpositionBlocks.COMPUTER.get()));
    public static final RegistryObject<BlockEntityType<MonitorBlockEntity>> SIGNAL_READOUT =
            registerBlockEntity("signal_readout",
                    () -> BlockEntityType.Builder.of(MonitorBlockEntity::new, SuperpositionBlocks.SIGNAL_READOUT.get()));
    public static final RegistryObject<BlockEntityType<AnalyserBlockEntity>> ANALYSER =
            registerBlockEntity("analyser",
                    () -> BlockEntityType.Builder.of(AnalyserBlockEntity::new, SuperpositionBlocks.ANALYSER.get()));
    public static final RegistryObject<BlockEntityType<CombinatorBlockEntity>> COMBINATOR =
            registerBlockEntity("combinator",
                    () -> BlockEntityType.Builder.of(CombinatorBlockEntity::new, SuperpositionBlocks.COMBINATOR.get()));

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(String name, Supplier<BlockEntityType.Builder<T>> blockEntity) {
        return BLOCK_ENTITIES.register(name, () -> blockEntity.get().build(null));
    }

    public static void bootstrap() {
    }
}