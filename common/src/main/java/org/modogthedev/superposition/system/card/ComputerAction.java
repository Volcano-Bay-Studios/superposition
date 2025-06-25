package org.modogthedev.superposition.system.card;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public interface ComputerAction extends ExecutableAction {
    void computer(List<Signal> signals, Level level, BlockPos pos, ComputerBlockEntity computerBlockEntity);

    @Override
    default List<Signal> execute(List<Signal> signals, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ComputerBlockEntity computerBlockEntity) {
            computer(signals, level, pos, computerBlockEntity);
        }
        return signals;
    }

    @Override
    default int getParameterCount() {
        return 1;
    }
}
