package org.modogthedev.superposition.system.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public interface ComputerAction extends ExecutableAction {
    void computer(List<Signal> signals, Level level, BlockPos pos);

    @Override
    default List<Signal> execute(List<Signal> signals, Level level, BlockPos pos) {
        computer(signals, level, pos);
        return signals;
    }

    @Override
    default int getParameterCount() {
        return 1;
    }
}
