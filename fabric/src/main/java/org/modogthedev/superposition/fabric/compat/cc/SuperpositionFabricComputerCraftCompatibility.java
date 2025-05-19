package org.modogthedev.superposition.fabric.compat.cc;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.compat.cc.CablePeripheral;
import org.modogthedev.superposition.system.cable.CablePassthroughManager;

public class SuperpositionFabricComputerCraftCompatibility {
    public static void setup() {
        PeripheralLookup.get().registerFallback((level, blockPos, blockState, blockEntity, direction) -> peripheralProvider(level, blockPos));
    }

    @Nullable
    public static IPeripheral peripheralProvider(Level level, BlockPos pos) {
        if (CablePassthroughManager.getSignalsFromBlock(level, pos) != null && !CablePassthroughManager.getSignalsFromBlock(level, pos).isEmpty()) {
            return new CablePeripheral(level, pos);
        }
        if (level.getBlockEntity(pos) instanceof ComputerBlockEntity computerBlockEntity) { //TODO: SLAVERY!!!
//            if (computerBlockEntity.getCard() instanceof SlaveCard) {
//                return new ComputerPeriphreal(computerBlockEntity);
//            }
        }
        return null;
    }

}
