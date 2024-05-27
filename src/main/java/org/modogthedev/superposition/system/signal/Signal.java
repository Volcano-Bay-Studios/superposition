package org.modogthedev.superposition.system.signal;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.system.antenna.Antenna;

import java.util.UUID;

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
    float maxRange;
    public float amplitude;
    public boolean emitting = true;
    public Antenna antenna;
    public UUID uuid = UUID.randomUUID();
    public EncodedData encodedData;

    public boolean tick() {
        for (float i = 0; i < 361; i += .1f) {
//            this.level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.x + (Math.sin(i)*maxDist), pos.y, pos.z+ (Math.cos(i)*maxDist), 0, 0, 0);
        }
        lifetime++;
        maxRange = amplitude*100;
        if (!emitting) {
            int endTicks = lifetime-endTime;
            minDist = endTicks*speed;
            if (minDist > maxRange) {
                return true;
            }
        }
        maxDist = Math.min(maxRange,lifetime*speed);
        return false;
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
