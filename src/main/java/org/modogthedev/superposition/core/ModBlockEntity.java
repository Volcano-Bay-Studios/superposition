package org.modogthedev.superposition.core;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.blockentity.SignalGeneratorBlockEntity;

public class ModBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Superposition.MODID);

    public static final RegistryObject<BlockEntityType<SignalGeneratorBlockEntity>> SIGNAL_GENERATOR =
            BLOCK_ENTITIES.register("signal_generator",
                    () -> BlockEntityType.Builder.of(SignalGeneratorBlockEntity::new, ModBlock.SIGNAL_GENERATOR.get())
                            .build(null));
    public static final RegistryObject<BlockEntityType<SignalGeneratorBlockEntity>> MODULATOR =
            BLOCK_ENTITIES.register("modulator",
                    () -> BlockEntityType.Builder.of(SignalGeneratorBlockEntity::new, ModBlock.MODULATOR.get())
                            .build(null));
}