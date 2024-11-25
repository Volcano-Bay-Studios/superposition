package org.modogthedev.superposition.fabric.mixin.self;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.bridge.CommonRedstone;
import org.modogthedev.superposition.fabric.util.ConnectableRedstoneBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommonRedstone.class)
public interface CommonRedstoneMixin extends CommonRedstone, ConnectableRedstoneBlock {
    default boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return this.commonConnectRedstone(state, level, pos, direction);
    }
}
