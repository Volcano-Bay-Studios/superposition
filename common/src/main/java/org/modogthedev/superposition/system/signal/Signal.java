package org.modogthedev.superposition.system.signal;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.modogthedev.superposition.system.signal.data.EncodedData;

import java.util.UUID;

public class Signal {

    public static final int SPEED = 64;

    private UUID id;
    private final Vector3d pos;
    private float amplitude;
    private float frequency;
    private float sourceFrequency;
    private float modulation;
    private boolean emitting = true;
    private int lifetime = 0;
    private BlockPos sourceAntennaPos;
    private int sourceAntennaSize = 0;
    private EncodedData<?> encodedData = null;

    @Deprecated
    public Level level;
    private int endTime = 0;
    private float maxDist = 0;
    private float minDist = 0;

    public boolean tick() {
//        for (float i = 0; i < 361; i += .1f) {
//            this.level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.x + (Math.sin(i)*maxDist), pos.y, pos.z+ (Math.cos(i)*maxDist), 0, 0, 0);
//        }
        lifetime++;
        float maxRange = amplitude * 100;
        if (!emitting) {
            int endTicks = lifetime - endTime - 2;
            minDist = endTicks * SPEED;
            if (minDist > maxRange) {
                return true;
            }
        }
        maxDist = Math.min(maxRange, lifetime * SPEED);
        return false;
    }

    public Signal(Vector3dc pos, Level level, float frequency, float amplitude, float sourceFrequency) {
        this.id = UUID.randomUUID();
        this.pos = new Vector3d(pos);
        this.level = level;
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.sourceFrequency = sourceFrequency;
        setSourceAntenna(new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z()),0);
    }

    public Signal(UUID id, FriendlyByteBuf buf) {
        this.pos = new Vector3d();
        this.load(id, buf);
    }

    public Signal(Signal signal) {
        this.pos = new Vector3d();
        this.copy(signal);
    }

    public void load(UUID id, FriendlyByteBuf buf) {
        this.id = id;
        this.pos.set(buf.readFloat(), buf.readFloat(), buf.readFloat());
        this.amplitude = buf.readFloat();
        this.frequency = buf.readFloat();
        this.sourceFrequency = buf.readFloat();
        this.modulation = buf.readFloat();
        this.emitting = buf.readBoolean();
        this.lifetime = buf.readVarInt();
        this.sourceAntennaPos = buf.readBlockPos();
        this.sourceAntennaSize = buf.readVarInt();
        int ordinal = buf.readVarInt();
        if (ordinal > 0) {
            this.encodedData = EncodedData.Type.values()[ordinal - 1].getCodec().decode(buf);
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.id);
        buf.writeFloat((float) this.pos.x);
        buf.writeFloat((float) this.pos.y);
        buf.writeFloat((float) this.pos.z);
        buf.writeFloat(this.amplitude);
        buf.writeFloat(this.frequency);
        buf.writeFloat(this.sourceFrequency);
        buf.writeFloat(this.modulation);
        buf.writeBoolean(this.emitting);
        buf.writeVarInt(this.lifetime);
        buf.writeBlockPos(this.sourceAntennaPos);
        buf.writeVarInt(sourceAntennaSize);
        if (this.encodedData != null) {
            EncodedData.Type type = this.encodedData.type();
            buf.writeVarInt(type.ordinal() + 1);
            type.getCodec().encode(buf, this.encodedData);
        } else {
            buf.writeVarInt(0);
        }
    }

    public void copy(Signal signal) {
        this.id = signal.id;
        this.modulation = signal.modulation;
        this.emitting = signal.emitting;
        this.lifetime = signal.lifetime;
        this.level = signal.level;
        this.frequency = signal.frequency;
        this.amplitude = signal.amplitude;
        this.pos.set(signal.pos);
        this.sourceFrequency = signal.sourceFrequency;
        this.sourceAntennaPos = signal.sourceAntennaPos;
        this.sourceAntennaSize = signal.sourceAntennaSize;
        this.encodedData = signal.encodedData;
    }

    public void encode(String string) {
        this.encodedData = EncodedData.of(string);
    }

    public void setEncodedData(EncodedData<?> encodedData) {
        this.encodedData = encodedData;
    }

    public void encode(int integer) {
        this.encodedData = EncodedData.of(integer);
    }

    public void encode(float f) {
        this.encodedData = EncodedData.of(f);
    }

    public void clearEncodedData() {
        this.encodedData = null;
    }

    public void stop() {
        if (this.emitting) {
            this.endTime = this.lifetime;
            this.emitting = false;
        }
    }

    public void modulate(float amplitude) {
        this.amplitude += amplitude;
    }

    public void mulAmplitude(float amplitude) {
        this.amplitude *= amplitude;
    }

    public UUID getId() {
        return this.id;
    }

    public Vector3d getPos() {
        return this.pos;
    }

    public float getAmplitude() {
        return this.amplitude;
    }

    public float getFrequency() {
        return this.frequency;
    }

    public float getSourceFrequency() {
        return this.sourceFrequency;
    }

    public float getModulation() {
        return this.modulation;
    }

    public boolean isEmitting() {
        return this.emitting;
    }

    public int getLifetime() {
        return this.lifetime;
    }

    public BlockPos getSourceAntennaPos() {
        return this.sourceAntennaPos;
    }

    public int getSourceAntennaSize() {
        return this.sourceAntennaSize;
    }
    @Nullable
    public EncodedData<?> getEncodedData() {
        return this.encodedData;
    }

    public int getEndTime() {
        return this.endTime;
    }

    public float getMinDist() {
        return this.minDist;
    }

    public float getMaxDist() {
        return this.maxDist;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public void setSourceFrequency(float sourceFrequency) {
        this.sourceFrequency = sourceFrequency;
    }

    public void setModulation(float modulation) {
        this.modulation = modulation;
    }

    public void setEmitting(boolean emitting) {
        this.emitting = emitting;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    public void setSourceAntenna(BlockPos sourceAntennaPos, int sourceAntennaSize) {
        this.sourceAntennaPos = sourceAntennaPos;
        this.sourceAntennaSize = sourceAntennaSize;
    }

    @Override
    public String toString() {
        return "Signal Frequency: " + this.frequency + " Amplitude: " + this.amplitude;
    }
}
