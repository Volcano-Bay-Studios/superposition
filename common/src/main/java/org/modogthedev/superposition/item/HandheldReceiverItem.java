package org.modogthedev.superposition.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.core.SuperpositionSounds;
import org.modogthedev.superposition.system.signal.ClientSignalManager;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.LongRaycast;
import org.modogthedev.superposition.util.SuperpositionMth;

public class HandheldReceiverItem extends Item {
    public HandheldReceiverItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected && level.isClientSide) {
            for (Signal signal : ClientSignalManager.clientSignals.get(level).values()) {
                float dist = (float) Vec3.atLowerCornerOf(entity.blockPosition()).distanceTo(Vec3.atLowerCornerOf(SuperpositionMth.blockPosFromVec3(signal.getPos())));
                if (dist < signal.getMaxDist() && dist > signal.getMinDist()) {
                    float pitch = SuperpositionMth.getFromRange(15000000, 0, 2, .72f, signal.getFrequency());
                    Vec3 vec31 = new Vec3(signal.getPos().x - entity.getX(), signal.getPos().y - entity.getEyeY(), signal.getPos().z - entity.getZ());
                    float volume = (float) Math.pow(Math.max(0, entity.getViewVector(0).normalize().dot(vec31.normalize())), 2) - 0.4f;
                    float penetration = LongRaycast.getPenetration(signal.level,signal.getPos(),new Vector3d(entity.getX(),entity.getY(),entity.getZ()));
                    volume *= Mth.map(penetration,0,signal.getFrequency()/200000,1,0);
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SuperpositionSounds.SINE.get(), pitch, volume));
                }
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }
}
