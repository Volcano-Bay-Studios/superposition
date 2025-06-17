package org.modogthedev.superposition.system.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;

public interface BiModifyAction extends ExecutableAction {
    Signal modify(Signal signal, Signal periphrealSignal);

    @Override
    default List<Signal> execute(List<Signal> signals, Level level, BlockPos pos) {
        List<Signal> returnSignals = new ArrayList<>();
        for (int i = 0 ; i < signals.size(); i += 2) {
            if (signals.size() >= 2) {
                returnSignals.add(modify(signals.get(i), signals.get(i+1)));
            }
        }
        return returnSignals;
    }

    @Override
    default int getParameterCount() { return 2; }
}
