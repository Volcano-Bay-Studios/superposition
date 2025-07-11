package org.modogthedev.superposition.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import org.modogthedev.superposition.system.world.RedstoneWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignalGetter.class)
public interface SignalGetterMixin {
    @Inject(method = "hasNeighborSignal", at = @At("HEAD"), cancellable = true)
    default void getSignal(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        int power = RedstoneWorld.getPower(((Level) this), pos);
        if (power > 0) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    default void getSignal(BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        int power = Math.max(RedstoneWorld.getPower(((Level) this), pos),RedstoneWorld.getPower(((Level) this), pos.relative(direction.getOpposite())));
        if (power > 0) {
            cir.setReturnValue(power);
        }
    }
}
