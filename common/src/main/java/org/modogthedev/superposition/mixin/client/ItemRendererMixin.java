package org.modogthedev.superposition.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.client.renderer.ui.SignalScopeRenderer;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Final
    @Shadow
    private ItemModelShaper itemModelShaper;

    @WrapOperation(method = "getModel",at  = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemModelShaper;getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;"))
    private BakedModel getModel(ItemModelShaper instance, ItemStack stack, Operation<BakedModel> original) {
        if (stack.is(SuperpositionItems.SIGNAL_SCOPE.get())) {
            return itemModelShaper.getModelManager().getModel(SignalScopeRenderer.SIGNAL_SCOPE_IN_HAND_MODEL);
        }
        return original.call(instance,stack);
    }
}
