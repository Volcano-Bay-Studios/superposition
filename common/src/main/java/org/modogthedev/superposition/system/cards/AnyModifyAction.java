package org.modogthedev.superposition.system.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public interface AnyModifyAction extends ExecutableAction {
    List<Signal> modify(List<Signal> signals);

    @Override
    default List<Signal> execute(List<Signal> signals, Level level, BlockPos pos) {
        return modify(signals);
    }

    @Override
    default int getParameterCount() { return 6; }
}
