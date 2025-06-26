package org.modogthedev.superposition.system.card.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.ExecutableAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class GroupAction extends Action implements ExecutableAction {
    public GroupAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public List<Signal> execute(List<Signal> signals, Level level, BlockPos pos) {
        return signals;
    }

    @Override
    public int getParameterCount() {
        return 2;
    }
}
