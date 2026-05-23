package org.modogthedev.superposition.system.card.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.NodePorts;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class SplitAction extends Action {
    public SplitAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public void execute(NodePorts input, NodePorts output, Level level, BlockPos pos) {
        List<Signal> signals = input.getSignals(inString());

        output.putSignals("a",signals);
        output.putSignals("b",signals);
    }

    @Override
    protected NodePorts.Builder buildOutputPorts(NodePorts.Builder builder) {
        return builder.addPort("a").addPort("b");
    }
}
