package org.modogthedev.superposition.mixin;

import net.minecraft.world.entity.player.Player;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow public abstract void tick();

    @Inject(method = "isScoping", at = @At("HEAD"), cancellable = true)
    private void isScoping(CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) ((Object)this);
        if (player.isUsingItem() && player.getUseItem().is(SuperpositionItems.SIGNAL_SCOPE.get())) {
        }
    }
}
