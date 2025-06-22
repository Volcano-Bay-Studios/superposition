package org.modogthedev.superposition.system.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;

public interface ManipulateAction extends PeriphrealAction {
    void manipulate(Signal signal, Level level, BlockPos pos);

    @Override
    default List<Signal> execute(List<Signal> signals, Level level, BlockPos pos) {
        List<Signal> returnSignals = new ArrayList<>();
        for (int i = 0; i < signals.size(); i += 2) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", getLocation().toString());
            addOutbound(tag, signals.get(i + 1));
            signals.get(i).encode(tag);
            returnSignals.add(signals.get(i));
        }
        return returnSignals;
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
