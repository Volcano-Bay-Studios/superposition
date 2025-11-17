package org.modogthedev.superposition.blockentity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalHelper;

import java.util.List;

public abstract class SimpleSignalProviderBlockEntity extends SignalActorBlockEntity {
    private Signal output = null;
    public SimpleSignalProviderBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    /**
     * @param signal The signal whose data should be modified
     * @param face The side of the block that the signal is being taken from
     */
    public abstract void encodeInformation(Signal signal, Direction face);

    @Override
    public List<Signal> getSideSignals(Direction face) {
        if (output == null) {
            output = SignalHelper.getEmptySignal(getLevel(),getBlockPos());
        }
        encodeInformation(output, face);
        return super.getSideSignals(face);
    }
}
