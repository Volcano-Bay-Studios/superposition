package org.modogthedev.superposition.system.signal;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Signal {
    public float frequency;
    public static final int speed = 10;
    public float modulation;
    public Vec3 pos;
    public Level level;
    public int lifetime = 0;
    public int endTime = 0;
    public float maxDist = 0;
    public float minDist = 0;
    public float amplitude;
    public boolean emitting = true;
    public Vec3 sourcePos;

    public boolean tick() {
        this.level.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 0, 0, 0);
        lifetime++;
        if (!emitting) {
            int endTicks = lifetime-endTime;
            minDist = endTicks*speed;
            if (endTicks > endTime) {
                return true;
            }
        }
        maxDist = lifetime*speed;
        return false;
    }

    protected CompoundTag addAdditionalSaveData() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        CompoundTag particle = new CompoundTag();
        particle.putDouble("x", pos.x);
        list.add(particle);
        tag.put("particle", list);
        return tag;
    }

    public Signal(Vec3 pos, Level level, float frequency, float amplitude) {
        this.pos = pos;
        this.level = level;
        this.frequency = frequency;
        this.amplitude = amplitude;
    }

    public void setModulation(float newModulation) {
        modulation = newModulation;
    }
}
