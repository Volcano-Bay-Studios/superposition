package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.core.SuperpositionActions;
import org.modogthedev.superposition.core.SuperpositionBlocks;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.ComputerAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class OutputAction extends Action implements ComputerAction {

    public OutputAction(ResourceLocation action, Information info) {
        super(action, info);
        getConfigurations().add(SuperpositionActions.DIRECTION.get());
    }

    @Override
    public void computer(List<Signal> signal, Level level, BlockPos pos) {

    }

    @Override
    public ItemStack getThumbnailItem() {
        return SuperpositionBlocks.COMPUTER.get().asItem().getDefaultInstance();
    }

    @Override
    public boolean hasOutput() {
        return false;
    }
}
