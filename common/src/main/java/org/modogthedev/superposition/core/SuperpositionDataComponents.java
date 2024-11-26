package org.modogthedev.superposition.core;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.modogthedev.superposition.Superposition;

import java.util.function.UnaryOperator;

public class SuperpositionDataComponents {
//    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Superposition.MODID, Registries.DATA_COMPONENT_TYPE);
//
//    public static final DataComponentType<BlockPos> COORDINATES =
//            register("coordinates", builder -> builder.persistent(BlockPos.CODEC));
//
//    private static <T>DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
//        return DATA_COMPONENT_TYPES.register(name,
//                () -> ((DataComponentType.Builder)builderOperator.apply(DataComponentType.builder())).build()).get();
//    }
}
