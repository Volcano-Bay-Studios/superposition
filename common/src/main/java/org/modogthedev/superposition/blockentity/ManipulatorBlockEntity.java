package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

public class ManipulatorBlockEntity extends PeriphrealBlockEntity {

    public ManipulatorBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.MANIPULATOR.get(), pos, state);
    }

    @Override
    public void tick() {
        resetTooltip();
        addTooltip("Manipulator Status:");
        addTooltip("Manipulating " + level.getBlockState(getFrontPos()).getBlock().getName().getString()+"...");
        super.tick();
    }

    public BlockPos getFrontPos() {
        return getBlockPos().relative(getBlockState().getValue(SignalActorTickingBlock.FACING));
    }
}
