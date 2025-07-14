package org.modogthedev.superposition.system.card.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.ComputerAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class ProgramAction extends Action implements ComputerAction {

    public ProgramAction(ResourceLocation action, Information info) {
        super(action, info);
    }


    @Override
    public void computer(List<Signal> signals, Level level, BlockPos pos, ComputerBlockEntity computerBlockEntity) {
        Signal signal = signals.getFirst();
        if (signal != null && computerBlockEntity.getCard() != null) {
            signal.encode(computerBlockEntity.getCard().save(new CompoundTag()));
        }
    }

    @Override
    public int getParameterCount() {
        return 0;
    }
}
