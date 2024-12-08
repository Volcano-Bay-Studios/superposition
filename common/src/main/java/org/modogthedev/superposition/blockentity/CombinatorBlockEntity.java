package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

public class CombinatorBlockEntity extends PeriphrealBlockEntity {
    public CombinatorBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.COMBINATOR.get(), pos, state);
    }

    @Override
    public void tick() {
        resetTooltip();
        addTooltip("Combinator Status:");
        super.tick();
    }

    public Direction getFacing() {
        return getBlockState().getValue(SignalActorTickingBlock.FACING);
    }
}
