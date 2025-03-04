package org.modogthedev.superposition.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.system.cable.CableManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Shadow
    protected abstract boolean startAttack();

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1), cancellable = true)
    private void startUseItem(CallbackInfo ci, @Local InteractionHand hand, @Local ItemStack stack) {
        if (!stack.isEmpty()) {
            return;
        }

        InteractionResult result = CableManager.playerEmptyClickEvent(this.player, this.level);
        if (result.consumesAction()) {
            if (result.shouldSwing()) {
                this.player.swing(hand);
            }
            ci.cancel();
        }
    }
}
