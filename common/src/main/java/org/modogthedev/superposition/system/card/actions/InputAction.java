package org.modogthedev.superposition.system.card.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.core.SuperpositionActions;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.NodePorts;

public class InputAction extends Action {

    public InputAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public void execute(NodePorts input, NodePorts output, Level level, BlockPos pos) {
        output.putSignals("out",input.getSignals("in"));
    }

    @Override
    protected void setupConfigurations() {
        getConfigurations().add(SuperpositionActions.PORT_CONFIGURATION.get().copy());
    }

    @Override
    public NodePorts.Builder buildInputPorts(NodePorts.Builder builder) {
        return builder.addVirtualPort(inString());
    }
}
