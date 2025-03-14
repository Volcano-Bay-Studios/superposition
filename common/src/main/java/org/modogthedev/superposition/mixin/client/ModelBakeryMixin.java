package org.modogthedev.superposition.mixin.client;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.modogthedev.superposition.client.renderer.ui.SignalScopeRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow protected abstract void loadSpecialItemModelAndDependencies(ModelResourceLocation modelLocation);

    private static int register = 0;

    @Inject(method = "loadSpecialItemModelAndDependencies", at = @At("HEAD"), cancellable = true)
    private void addModels(ModelResourceLocation modelLocation, CallbackInfo ci) {
        if (register == 0) {
            register++;
            loadSpecialItemModelAndDependencies(SignalScopeRenderer.SIGNAL_SCOPE_IN_HAND_MODEL);
            return;
        }
        if (register >= 1) {
            register = 0;
        }
    }
}
