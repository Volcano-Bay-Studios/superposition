package org.modogthedev.superposition.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerItemInHandLayer.class)
public abstract class PlayerItemInHandLayerMixin {

    @Shadow protected abstract void renderArmWithSpyglass(LivingEntity entity, ItemStack stack, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int combinedLight);

    @Inject(method = "renderArmWithItem",at = @At("HEAD"), cancellable = true)
    private void renderArmWithItem(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (itemStack.is(SuperpositionItems.SIGNAL_SCOPE.get()) && livingEntity.getUseItem() == itemStack && livingEntity.swingTime == 0) {
            renderArmWithSpyglass(livingEntity,itemStack,arm,poseStack,buffer,packedLight);
            ci.cancel();
        }
    }
}
