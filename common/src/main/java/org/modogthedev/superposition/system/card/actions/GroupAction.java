package org.modogthedev.superposition.system.card.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.NodePorts;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;

public class GroupAction extends Action {
    public GroupAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    protected NodePorts.Builder buildInputPorts(NodePorts.Builder builder) {
        return builder.addPort("first").addPort("second");
    }

    @Override
    public void execute(NodePorts input, NodePorts output, Level level, BlockPos pos) {
        List<Signal> signals = new ArrayList<>();
        signals.addAll(input.getSignals("first"));
        signals.addAll(input.getSignals("second"));
        output.putSignals(outString(),signals);
    }
}
