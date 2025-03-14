package org.modogthedev.superposition.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.client.renderer.ui.SignalScopeRenderer;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Final
    @Shadow
    private ItemModelShaper itemModelShaper;


    @WrapMethod(method = "render")
    private void render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, Operation<Void> original) {
        boolean flag = displayContext == ItemDisplayContext.GUI || displayContext == ItemDisplayContext.GROUND || displayContext == ItemDisplayContext.FIXED;
        if (flag && itemStack.is(SuperpositionItems.SIGNAL_SCOPE.get())) {
            BakedModel spyglassModel = itemModelShaper.getModelManager().getModel(SignalScopeRenderer.SIGNAL_SCOPE_MODEL);
            original.call(itemStack,displayContext,leftHand,poseStack,bufferSource,combinedLight,combinedOverlay,spyglassModel);
        } else {
            original.call(itemStack,displayContext,leftHand,poseStack,bufferSource,combinedLight,combinedOverlay,model);
        }
    }
}
