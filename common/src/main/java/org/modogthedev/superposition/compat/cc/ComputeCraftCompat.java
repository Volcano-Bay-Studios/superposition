package org.modogthedev.superposition.compat.cc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ComputeCraftCompat {
    public static void register() {
        registerWithDependency();
    }
    public static void registerWithDependency() {
//        PeriphrealLookup.get().registerFallback((level, blockPos, blockState, blockEntity, direction) -> peripheralProvider(level, blockPos));
    }
}
