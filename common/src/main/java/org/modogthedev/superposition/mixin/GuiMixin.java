package org.modogthedev.superposition.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.modogthedev.superposition.client.renderer.ui.SignalScopeRenderer;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true)
    private void renderSpyglassOverlay(GuiGraphics guiGraphics, float scopeScale, CallbackInfo ci) {
        if (Minecraft.getInstance().player.getUseItem().is(SuperpositionItems.SIGNAL_SCOPE.get())) {
            SignalScopeRenderer.renderSignalScope(guiGraphics, scopeScale);
            ci.cancel();
        }
    }
}
