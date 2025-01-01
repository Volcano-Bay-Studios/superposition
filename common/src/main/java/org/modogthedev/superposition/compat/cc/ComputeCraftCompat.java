package org.modogthedev.superposition.compat.cc;

import dan200.computercraft.api.ComputerCraftAPI;

public class ComputeCraftCompat {
    public static void register() {
        registerWithDependency();
    }
    public static void registerWithDependency() {
//        PeriphrealLookup.get().registerFallback((level, blockPos, blockState, blockEntity, direction) -> peripheralProvider(level, blockPos));
    }
}
