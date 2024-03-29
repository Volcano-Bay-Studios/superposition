package org.modogthedev.superposition.particle;

import net.minecraft.client.particle.DustParticle;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Particle {
    public Vec3 pos;
    public Vec3 vel;
    public Level level;
    public int lifetime = 0;
    public TYPE type;
    public float frequency;
    public float quanta;

    public void tick() {
        this.level.addParticle(ParticleTypes.FLAME, pos.x, pos.y+1, pos.z, 0, 0, 0);
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

    public Particle(Vec3 pos, Level level, float frequency, float quanta, Vec3 vel) {
        this.pos = pos;
        this.level = level;
        this.frequency = frequency;
        this.quanta = quanta;
        this.vel = vel.normalize();
    }

    public enum TYPE {
        RADIO,
        MICROWAVE,
        INFRARED,
        VISIBLE,
        ULTRAVIOLET,
        XRAY,
        GAMMARAY
    }
}
