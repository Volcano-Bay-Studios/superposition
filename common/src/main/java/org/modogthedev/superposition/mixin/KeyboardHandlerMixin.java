package org.modogthedev.superposition.mixin;

import net.minecraft.client.KeyboardHandler;
import org.lwjgl.glfw.GLFW;
import org.modogthedev.superposition.client.renderer.ui.SuperpositionUITooltipRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({KeyboardHandler.class})
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void keyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (SuperpositionUITooltipRenderer.editingEditable && action != GLFW.GLFW_RELEASE) {
            ci.cancel();
        }
        SuperpositionUITooltipRenderer.keyPress(windowPointer,key,scanCode,action,modifiers);
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void charTyped(long windowPointer, int codePoint, int modifiers, CallbackInfo ci) {
        if (SuperpositionUITooltipRenderer.editingEditable) {
            ci.cancel();
        }
        if (Character.charCount(codePoint) == 1) {
            SuperpositionUITooltipRenderer.charTyped(windowPointer,(char) codePoint,modifiers);
        } else {
            for (char c0 : Character.toChars(codePoint)) {
                SuperpositionUITooltipRenderer.charTyped(windowPointer,c0,modifiers);
            }
        }
    }
}
