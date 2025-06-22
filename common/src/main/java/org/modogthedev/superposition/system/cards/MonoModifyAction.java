package org.modogthedev.superposition.system.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public interface MonoModifyAction extends ExecutableAction {
    Signal modify(Signal signal);

    @Override
    default List<Signal> execute(List<Signal> signals, Level level, BlockPos pos) {
        for (Signal signal : signals) {
            modify(signals.getFirst());
        }
        return signals;
    }

    @Override
    default int getParameterCount() {
        return 1;
    }
}
