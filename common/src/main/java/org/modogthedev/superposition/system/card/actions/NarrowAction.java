package org.modogthedev.superposition.system.card.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.NodePorts;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalHelper;

import java.util.List;

public class NarrowAction extends Action {
    public NarrowAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public void execute(NodePorts input, NodePorts output, Level level, BlockPos pos) {
        List<Signal> signals = input.getSignals(inString());
        if (!signals.isEmpty()) {
            output.putSignals(outString(), SignalHelper.listOf(signals.getFirst()));
        }
    }
}
