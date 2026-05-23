package org.modogthedev.superposition.system.card;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;

public abstract class ComputerAction extends Action {
    public ComputerAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    protected abstract void computer(NodePorts input, Level level, BlockPos pos, ComputerBlockEntity computerBlockEntity);

    @Override
    public void execute(NodePorts input, NodePorts output, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ComputerBlockEntity computerBlockEntity) {
            computer(input, level, pos, computerBlockEntity);
        }
    }
}
