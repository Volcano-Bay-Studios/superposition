package org.modogthedev.superposition.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.modogthedev.superposition.client.renderer.ui.SignalScopeRenderer;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandelerMixin {

    @Inject(method = "onScroll", at = @At("HEAD"),cancellable = true)
    public void scroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci) {
        if (windowPointer == Minecraft.getInstance().getWindow().getWindow()) {
            if (Minecraft.getInstance().player.getUseItem().is(SuperpositionItems.SIGNAL_SCOPE.get())) {
                SignalScopeRenderer.scroll((float) yOffset);
                ci.cancel();
            }
        }
    }
}
