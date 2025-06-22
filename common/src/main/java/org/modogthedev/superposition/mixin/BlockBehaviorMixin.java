package org.modogthedev.superposition.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.system.world.RedstoneWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class BlockBehaviorMixin {
    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    private void getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        int power = RedstoneWorld.getPower((Level) level, pos);
        if (power > 0) {
            cir.setReturnValue(power);
        }
    }
}
