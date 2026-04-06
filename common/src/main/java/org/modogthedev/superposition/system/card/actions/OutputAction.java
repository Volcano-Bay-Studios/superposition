package org.modogthedev.superposition.system.card.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.core.SuperpositionActions;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.ComputerAction;
import org.modogthedev.superposition.system.card.actions.configuration.PortConfiguration;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class OutputAction extends Action implements ComputerAction {

    public OutputAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    protected void setupConfigurations() {
        getConfigurations().add(SuperpositionActions.DIRECTION_CONFIGURATION.get().copy());
    }

    @Override
    public void computer(List<Signal> signal, Level level, BlockPos pos, ComputerBlockEntity computerBlockEntity) {
        if (getConfigurations().getFirst() instanceof PortConfiguration portConfiguration) {
            computerBlockEntity.addOutbound(portConfiguration.getString(), signal);
        }
    }

    @Override
    public int getOutputCount() {
        return 0;
    }
}
