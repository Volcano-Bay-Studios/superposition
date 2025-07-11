package org.modogthedev.superposition.system.card.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.ExecutableAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class SplitAction extends Action implements ExecutableAction {
    public SplitAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public List<Signal> execute(List<Signal> signals, Level level, BlockPos pos) {
        return signals;
    }

    @Override
    public int getParameterCount() {
        return 1;
    }

    @Override
    public int getOutputCount() {
        return 2;
    }

    @Override
    public boolean sameOutput() {
        return true;
    }
}
