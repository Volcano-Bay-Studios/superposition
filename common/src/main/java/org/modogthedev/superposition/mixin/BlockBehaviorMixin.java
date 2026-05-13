package org.modogthedev.superposition.mixin;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockBehaviour.class)
public class BlockBehaviorMixin {
//    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
//    private void getSignal(BlockState state, BlockGetter level, BlockPos blockPos, Direction direction, CallbackInfoReturnable<Integer> cir) {
//        int power = RedstoneWorld.getPower((Level) level, blockPos);
//        if (power > 0) {
//            cir.setReturnValue(power);
//        }
//    }
}
