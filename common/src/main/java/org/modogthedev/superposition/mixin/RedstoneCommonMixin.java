package org.modogthedev.superposition.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import org.modogthedev.superposition.util.IRedstoneConnectingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedStoneWireBlock.class)
public class RedstoneCommonMixin {
    @Inject(method = "getConnectingSide(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/level/block/state/properties/RedstoneSide;", at = @At("HEAD"), cancellable = true)
    private void getConnectingSide(BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<RedstoneSide> cir) {
        BlockPos blockPos = pos.relative(direction);
        BlockState blockState = level.getBlockState(blockPos);

        if (blockState.getBlock() instanceof IRedstoneConnectingBlock block) {
            if (block.canConnectRedstone(blockState, level, blockPos, direction)) {
                cir.setReturnValue(RedstoneSide.SIDE);
            }
        }
    }
}
