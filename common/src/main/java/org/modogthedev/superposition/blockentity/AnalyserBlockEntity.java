package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

public class AnalyserBlockEntity extends PeriphrealBlockEntity {
    public int distance = 0;

    public AnalyserBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.ANALYSER.get(), pos, state);
    }

    @Override
    public void tick() {
        resetTooltip();
        updateDistance();
        addTooltip("Analyser Status:");
        addTooltip("Targeted Block - " + level.getBlockState(getAnalysisPosition()).getBlock().getName().getString());
        addTooltip("Distance - "+ distance + " Blocks");
        super.tick();
    }

    public void updateDistance() {
        for (int i = 1; i <= SuperpositionConstants.analyserRange; i++) {
            BlockPos pos = getBlockPos().relative(getBlockState().getValue(SignalActorTickingBlock.FACING), i);
            assert level != null;
            if (!level.getBlockState(pos).is(Blocks.AIR)) {
                distance = i;
                return;
            }
        }
        distance = SuperpositionConstants.analyserRange;
    }

    public Direction getFacing() {
        return getBlockState().getValue(SignalActorTickingBlock.FACING);
    }

    public BlockPos getAnalysisPosition() {
        return getBlockPos().relative(getBlockState().getValue(SignalActorTickingBlock.FACING), distance);
    }
}
