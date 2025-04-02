package org.modogthedev.superposition.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.modogthedev.superposition.client.renderer.ui.SignalScopeRenderer;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.modogthedev.superposition.system.signal.ClientSignalManager;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.sound.ClientAudioManager;
import org.modogthedev.superposition.util.LongRaycast;
import org.modogthedev.superposition.util.SuperpositionMth;

public class SignalScopeItem extends SpyglassItem {
    public SignalScopeItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPYGLASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
        player.awardStat(Stats.ITEM_USED.get(this));
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        this.stopUsing(livingEntity);
        return stack;
    }


    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if ((isSelected || (entity instanceof Player player && player.getOffhandItem().is(SuperpositionItems.SIGNAL_SCOPE.get()))) && level.isClientSide && ClientSignalManager.clientSignals.get(level) != null) {
            ClientAudioManager.signals.clear();
            for (Signal signal : ClientSignalManager.clientSignals.get(level).values()) {
                float dist = (float) Vec3.atLowerCornerOf(entity.blockPosition()).distanceTo(Vec3.atLowerCornerOf(SuperpositionMth.blockPosFromVec3(signal.getPos())));
                if (dist < signal.getMaxDist() && dist > signal.getMinDist()) {
                    float falloff = Math.min(signal.getFrequency() / 100000 - (SignalScopeRenderer.position - SignalScopeRenderer.selectorWidth), (SignalScopeRenderer.position + SignalScopeRenderer.selectorWidth) - signal.getFrequency() / 100000);
                    float pitch = SuperpositionMth.getFromRange(15000000, 0, 2, .72f, signal.getFrequency());
                    Vec3 vec31 = new Vec3(signal.getPos().x - entity.getX(), signal.getPos().y - entity.getEyeY(), signal.getPos().z - entity.getZ());
                    float volume = signal.getAmplitude();
                    float penetration = LongRaycast.getPenetration(signal.level, signal.getPos(), new Vector3d(entity.getX(), entity.getY(), entity.getZ()));
                    volume *= Mth.map(penetration, 0, signal.getFrequency() / 200000, 1, 0);
                    volume *= 1.0F / (Math.max(1, dist / (1000000000 / signal.getFrequency())));
                    Signal signal1 = new Signal(signal);
                    volume = Math.min(volume,2);
                    volume *= (float) Math.log(Math.max(0, entity.getViewVector(0).normalize().dot(vec31.normalize()))+1);
                    if (falloff < 0) {
                        volume /= -falloff;
                    }
                    signal1.setAmplitude(volume);
                    ClientAudioManager.signals.add(signal1);
                }
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        this.stopUsing(livingEntity);
    }

    private void stopUsing(LivingEntity user) {
        user.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
    }
}
