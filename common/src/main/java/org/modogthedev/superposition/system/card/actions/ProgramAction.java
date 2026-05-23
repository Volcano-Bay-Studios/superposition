package org.modogthedev.superposition.system.card.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.ComputerAction;
import org.modogthedev.superposition.system.card.NodePorts;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class ProgramAction extends ComputerAction {

    public ProgramAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    protected void computer(NodePorts input, Level level, BlockPos pos, ComputerBlockEntity computerBlockEntity) {
        Signal signal = input.getSignals(inString()).getFirst();
        if (signal != null && computerBlockEntity.getCard() != null) {
            signal.encode(computerBlockEntity.getCard().save(new CompoundTag()));
        }
    }

    @Override
    protected NodePorts.Builder buildInputPorts(NodePorts.Builder builder) {
        return builder.addVirtualPort(inString());
    }
}
