package org.modogthedev.superposition.mixin;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.modogthedev.superposition.networking.packet.PlayerAttackUseC2SPacket;
import org.modogthedev.superposition.system.cable.CableManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Nullable
    private GameType previousLocalPlayerMode;

    @Shadow
    public abstract boolean hasInfiniteItems();

    @Shadow
    private int destroyDelay;

    @Inject(method = "performUseItemOn", at = @At("HEAD"), cancellable = true)
    public void performUseItemOnHead(LocalPlayer player, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.level().getBlockEntity(result.getBlockPos()) instanceof SignalActorBlockEntity) {
            InteractionResult value = CableManager.playerUseEvent(player, result.getBlockPos(), result.getDirection());
            if (value.consumesAction()) {
                cir.setReturnValue(value);
            }
        }
    }


    @Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void onBlockBreakStart(final BlockPos blockPos, final Direction direction, final CallbackInfoReturnable<Boolean> cir) {
        assert this.minecraft.player != null;

        ItemStack itemStack = minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
        if (this.minecraft.hitResult instanceof final BlockHitResult blockHitResult) {
            if (itemStack.is(SuperpositionItems.SCREWDRIVER.get())) {
                if (SuperpositionItems.SCREWDRIVER.get().attackUse(blockHitResult.getBlockPos(), blockHitResult.getLocation(), minecraft.player, itemStack)) {
                    destroyDelay = 5;
                    cir.cancel();
                }
            } else if (minecraft.level.getBlockEntity(blockHitResult.getBlockPos()) instanceof PanelBlockEntity panelBlockEntity) {
                if (panelBlockEntity.primaryInteract(minecraft.player.isShiftKeyDown(), blockHitResult.getLocation().toVector3f())) {
                    VeilPacketManager.server().sendPacket(new PlayerAttackUseC2SPacket(blockPos,blockHitResult.getLocation()));
                }
            }
        }
    }

    @Inject(method = "performUseItemOn", at = @At("RETURN"), cancellable = true)
    public void performUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue().consumesAction()) {
            return;
        }

        InteractionResult value = CableManager.playerUseEvent(player, result.getBlockPos(), result.getDirection());
        if (value.consumesAction()) {
            cir.setReturnValue(value);
        }
    }
}
