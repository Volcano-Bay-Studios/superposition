package org.modogthedev.superposition.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Final
    @Shadow
    private ItemModelShaper itemModelShaper;

    @Unique
    private static final ModelResourceLocation SIGNAL_SCOPE_MODEL = ModelResourceLocation.inventory(Superposition.id("signal_scope"));

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        if (itemStack.is(SuperpositionItems.SIGNAL_SCOPE.get())) {
            BakedModel model1 = this.itemModelShaper.getModelManager().getModel(SIGNAL_SCOPE_MODEL);
            if (model != model1) {
                model = model1;
                ((ItemRenderer) (Object) this).render(itemStack, displayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, model);
                ci.cancel();
            }
        }
    }
}
