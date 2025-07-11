package org.modogthedev.superposition.system.card;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public interface ManipulateAction extends PeriphrealAction {

    @Override
    default List<Signal> execute(List<Signal> signals, Level level, BlockPos pos) {
    return signals;
    }

    /**
     * Encodes extra data to be used by Manipulators
     *
     * @param tag    The tag that is being sent to the peripheral
     * @param signal The current peripheral signal
     */
    void addOutbound(CompoundTag tag, Signal signal);

    @Override
    default int getParameterCount() {
        return 1;
    }
}
