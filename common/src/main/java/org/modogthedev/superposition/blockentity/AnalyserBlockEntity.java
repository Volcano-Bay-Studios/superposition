package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.core.SuperpositionTags;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

public class AnalyserBlockEntity extends PeripheralBlockEntity {
    public int distance = 0;
    public int startDistance = 0;
    public int endDistance = 0;

    public AnalyserBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.ANALYSER.get(), pos, state);
    }

    @Override
    public void tick() {
        resetTooltip();
        updateDistance();
        addTooltip("Analyser Status:");
        addTooltip("Analyzing " + level.getBlockState(getAnalysisPosition()).getBlock().getName().getString() + "...");
        addTooltip("Distance - " + distance + " Blocks");
        super.tick();
    }

    public void updateDistance() {
        int rangeRatio = 0;
        if (level.getBlockState(getBlockPos().relative(getBlockState().getValue(SignalActorTickingBlock.FACING))).is(SuperpositionTags.GLASS_BLOCKS)) {
            rangeRatio = 1;
        }
        for (int i = 1 + (SuperpositionConstants.analyserRange * rangeRatio); i <= SuperpositionConstants.analyserRange + (SuperpositionConstants.analyserRange * rangeRatio); i++) {
            BlockPos pos = getBlockPos().relative(getBlockState().getValue(SignalActorTickingBlock.FACING), i);
            assert level != null;
            if (!level.getBlockState(pos).is(Blocks.AIR) && !level.getBlockState(pos).is(SuperpositionTags.GLASS_BLOCKS)) {
                startDistance = 1 + (SuperpositionConstants.analyserRange * rangeRatio);
                endDistance = SuperpositionConstants.analyserRange + (SuperpositionConstants.analyserRange * rangeRatio);
                distance = i;
                return;
            }
        }
        startDistance = 1 + (SuperpositionConstants.analyserRange * rangeRatio);
        endDistance = SuperpositionConstants.analyserRange + (SuperpositionConstants.analyserRange * rangeRatio);
        distance = endDistance;
    }

    public Direction getFacing() {
        return getBlockState().getValue(SignalActorTickingBlock.FACING);
    }

    public BlockPos getDistancePosition(int distance) {
        return getBlockPos().relative(getBlockState().getValue(SignalActorTickingBlock.FACING), distance);
    }

    public BlockPos getAnalysisPosition() {
        return getDistancePosition(distance);
    }
}
