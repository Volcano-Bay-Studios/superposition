package org.modogthedev.superposition.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.system.signal.Signal;

public class SignalActorBlockEntity extends SyncedBlockEntity {
    public SignalActorBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public Signal modulateSignal(Signal signal) {
        return signal;
    }
    public Signal createSignal() {
        return null;
    }
}
