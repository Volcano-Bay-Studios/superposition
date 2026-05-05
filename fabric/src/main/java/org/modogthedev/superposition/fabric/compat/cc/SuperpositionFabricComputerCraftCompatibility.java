package org.modogthedev.superposition.fabric.compat.cc;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.compat.cc.PortInterfacePeripheral;
import org.modogthedev.superposition.util.PortBehavior;

public class SuperpositionFabricComputerCraftCompatibility {
    public static void setup() {
        PeripheralLookup.get().registerFallback((level, blockPos, blockState, blockEntity, direction) -> peripheralProvider(level, blockPos));
    }

    @Nullable
    public static IPeripheral peripheralProvider(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof PortBehavior behavior) {
            return new PortInterfacePeripheral(behavior);
        }
        return null;
    }

}
