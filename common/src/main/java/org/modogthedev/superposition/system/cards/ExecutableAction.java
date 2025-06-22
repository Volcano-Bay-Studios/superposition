package org.modogthedev.superposition.system.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public interface ExecutableAction {
    List<Signal> execute(List<Signal> signals, Level level, BlockPos pos);

    int getParameterCount();

    default boolean hasOutput() {
        return true;
    }
}
