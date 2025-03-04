package org.modogthedev.superposition.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionTags;
import org.modogthedev.superposition.system.cable.CableManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerGameModeMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    public void performUseItemOnHead(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir) {
        if (level.getBlockEntity(result.getBlockPos()) instanceof SignalActorBlockEntity || level.getBlockState(result.getBlockPos()).is(SuperpositionTags.PLACEABLE)) {
            InteractionResult value = CableManager.playerUseEvent(player, result.getBlockPos(), result.getDirection());
            if (value.consumesAction()) {
                cir.setReturnValue(value);
            }
        }
    }


    @Inject(method = "useItemOn", at = @At("RETURN"), cancellable = true)
    public void performUseItemOn(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue().consumesAction()) {
            return;
        }

        InteractionResult value = CableManager.playerUseEvent(player, result.getBlockPos(), result.getDirection());
        if (value.consumesAction()) {
            cir.setReturnValue(value);
        }
    }
}
