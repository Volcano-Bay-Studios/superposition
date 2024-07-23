package org.modogthedev.superposition.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IRedstoneConnectingBlock {
    boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction);
}
