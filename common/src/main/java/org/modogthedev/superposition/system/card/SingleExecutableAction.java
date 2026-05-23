package org.modogthedev.superposition.system.card;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public abstract class SingleExecutableAction extends Action {
    public SingleExecutableAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public void execute(NodePorts input, NodePorts output, Level level, BlockPos pos) {
        List<Signal> in = input.getSignals("in");
        for (Signal signal : in) {
            Signal out = modify(signal);
            if (out != null) {
                output.putSignal("out", out);
            }
        }
    }

    protected abstract Signal modify(Signal signal);
}
