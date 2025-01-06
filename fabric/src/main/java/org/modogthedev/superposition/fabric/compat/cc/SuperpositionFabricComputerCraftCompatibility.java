package org.modogthedev.superposition.fabric.compat.cc;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.compat.cc.CablePeripheral;
import org.modogthedev.superposition.compat.cc.SignalActorPeripheral;
import org.modogthedev.superposition.system.cable.CablePassthroughManager;

import javax.annotation.Nullable;

public class SuperpositionFabricComputerCraftCompatibility {
    public static void setup() {
        PeripheralLookup.get().registerFallback((level, blockPos, blockState, blockEntity, direction) -> peripheralProvider(level, blockPos,direction));
    }

    @Nullable
    public static IPeripheral peripheralProvider(Level level, BlockPos pos, Direction direction) {
        if (CablePassthroughManager.getSignalsFromBlock(level,pos) != null && !CablePassthroughManager.getSignalsFromBlock(level,pos).isEmpty()) {
            return new CablePeripheral(level,pos);
        }
        if (level.getBlockEntity(pos) instanceof SignalActorBlockEntity signalActorBlockEntity) {
            return new SignalActorPeripheral(signalActorBlockEntity,direction);
        }
        return null;
    }
}
