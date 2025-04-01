package org.modogthedev.superposition.fabric.mixin.self;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.world.item.CreativeModeTab;
import org.modogthedev.superposition.platform.SuperpositionPlatformHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SuperpositionPlatformHelper.class)
public class PlatformHelperMixin {

    @Inject(method = "creativeTabBuilder", at = @At("RETURN"), cancellable = true)
    private static void creativeTab(CallbackInfoReturnable<CreativeModeTab.Builder> cir) {
        cir.setReturnValue(FabricItemGroup.builder());
    }

}
