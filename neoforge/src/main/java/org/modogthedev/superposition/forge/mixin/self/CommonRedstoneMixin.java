package org.modogthedev.superposition.forge.mixin.self;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.bridge.CommonRedstone;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommonRedstone.class)
public interface CommonRedstoneMixin extends CommonRedstone, IBlockExtension {

    @Override
    default boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return this.commonConnectRedstone(state, level, pos, direction);
    }
}
