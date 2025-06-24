package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.core.SuperpositionActions;
import org.modogthedev.superposition.core.SuperpositionBlocks;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.ComputerAction;
import org.modogthedev.superposition.system.cards.ExecutableAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class InputAction extends Action implements ExecutableAction {

    public InputAction(ResourceLocation action, Information info) {
        super(action, info);
        getConfigurations().add(SuperpositionActions.DIRECTION.get());
    }

    @Override
    public ItemStack getThumbnailItem() {
        return SuperpositionBlocks.COMPUTER.get().asItem().getDefaultInstance();
    }

    @Override
    public List<Signal> execute(List<Signal> signals, Level level, BlockPos pos) {
        return List.of();
    }

    @Override
    public int getParameterCount() {
        return 0;
    }
}
