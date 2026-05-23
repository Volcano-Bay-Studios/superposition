package org.modogthedev.superposition.system.card.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.Card;
import org.modogthedev.superposition.system.card.ComputerAction;
import org.modogthedev.superposition.system.card.NodePorts;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class ReprogramAction extends ComputerAction {

    public ReprogramAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    protected void computer(NodePorts input, Level level, BlockPos pos, ComputerBlockEntity computerBlockEntity) {
        Signal signal = input.getSignals(inString()).getFirst();
        if (signal != null && signal.getEncodedData() != null && signal.getEncodedData().compoundTagData() != null) {
            CompoundTag tag = signal.getEncodedData().compoundTagData();
            Card card = new Card(tag);
            computerBlockEntity.setCard(card);
        }
    }

    @Override
    protected NodePorts.Builder buildOutputPorts(NodePorts.Builder builder) {
        return builder.addVirtualPort(outString());
    }
}
