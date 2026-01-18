package org.modogthedev.superposition.system.signal;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.modogthedev.superposition.system.signal.data.EncodedData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Signal {

    public static final int SPEED = 64;
    /**
     * This UUID is for keeping track of the signal itself.
     */
    private UUID uuid;
    /**
     * This UUID is for tracking multiple signals that are identical other than that they are broadcast from different antenna elements
     */
    private UUID broadcastUuid;
    private final Vector3d pos;
    private float amplitude;
    private float frequency;
    @Deprecated
    private float sourceFrequency;
    private boolean emitting = true;
    private int lifetime = 0;
    @Deprecated
    private BlockPos sourceAntennaPos;
    private EncodedData<?> encodedData = null;
    private float distance = 0;
    @Deprecated
    public Level level;
    private int endTime = 0;
    private float maxDist = 0;
    private float minDist = 0;

    public boolean tick() {
        lifetime++;
        float maxRange = amplitude * 5000;
        minDist = 0;
        if (!emitting) {
            int endTicks = lifetime - endTime - 2;
            minDist = endTicks * SPEED;
            if (minDist > maxRange) {
                return true;
            }
        }
        maxDist = lifetime * SPEED;
        return false;
    }

    public Signal(Vector3dc pos, Level level, float frequency, float amplitude, float sourceFrequency) {
        this.uuid = UUID.randomUUID();
        this.broadcastUuid = UUID.randomUUID();
        this.pos = new Vector3d(pos);
        this.level = level;
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.sourceFrequency = sourceFrequency;
        setSourceAntenna(new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z()));
    }

    public Signal(Level level, UUID uuid, FriendlyByteBuf buf) {
        this.pos = new Vector3d();
        this.load(uuid, buf);
    }

    public Signal(Signal signal) {
        this.pos = new Vector3d();
        this.copy(signal);
    }

    public void load(UUID id, FriendlyByteBuf buf) {
        this.uuid = id;
        this.broadcastUuid = buf.readUUID();
        this.pos.set(buf.readFloat(), buf.readFloat(), buf.readFloat());
        this.amplitude = buf.readFloat();
        this.frequency = buf.readFloat();
        this.sourceFrequency = buf.readFloat();
        this.emitting = buf.readBoolean();
        this.lifetime = buf.readVarInt();
        this.sourceAntennaPos = buf.readBlockPos();
        int ordinal = buf.readVarInt();
        if (ordinal > 0) {
            this.encodedData = EncodedData.Type.values()[ordinal - 1].getCodec().decode(buf);
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.uuid);
        buf.writeUUID(this.broadcastUuid);
        buf.writeFloat((float) this.pos.x);
        buf.writeFloat((float) this.pos.y);
        buf.writeFloat((float) this.pos.z);
        buf.writeFloat(this.amplitude);
        buf.writeFloat(this.frequency);
        buf.writeFloat(this.sourceFrequency);
        buf.writeBoolean(this.emitting);
        buf.writeVarInt(this.lifetime);
        buf.writeBlockPos(this.sourceAntennaPos);
        if (this.encodedData != null) {
            EncodedData.Type type = this.encodedData.type();
            buf.writeVarInt(type.ordinal() + 1);
            type.getCodec().encode(buf, this.encodedData);
        } else {
            buf.writeVarInt(0);
        }
    }

    public void copy(Signal signal) {
        this.level = signal.level;
        this.uuid = signal.uuid;
        this.broadcastUuid = signal.broadcastUuid;
        this.emitting = signal.emitting;
        this.lifetime = signal.lifetime;
        this.frequency = signal.frequency;
        this.amplitude = signal.amplitude;
        this.pos.set(signal.pos);
        this.sourceFrequency = signal.sourceFrequency;
        this.sourceAntennaPos = signal.sourceAntennaPos;
        this.encodedData = signal.encodedData;
    }

    public void encode(boolean bool) {
        this.encodedData = EncodedData.of(bool);
    }

    public void encode(String string) {
        this.encodedData = EncodedData.of(string);
    }

    public void encode(CompoundTag compoundTag) {
        this.encodedData = EncodedData.of(compoundTag);
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

    public void addAmplitude(float amplitude) {
        this.amplitude += amplitude;
    }

    public void mulAmplitude(float amplitude) {
        this.amplitude *= amplitude;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public UUID getBroadcastUuid() {
        return broadcastUuid;
    }

    /**
     * Resets the UUID of a signal if it already exists and is closing
     */
    public void changeUUID() {
        this.uuid = UUID.randomUUID();
    }

    /**
     * Resets the Broadcast UUID of a signal if it already exists and is closing
     */
    public void changeBroadcastUUID() {
        this.broadcastUuid = UUID.randomUUID();
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

    public boolean isEmitting() {
        return this.emitting;
    }

    public BlockPos getSourceAntennaPos() {
        return this.sourceAntennaPos;
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

    public void setEmitting(boolean emitting) {
        this.emitting = emitting;
    }

    public void addTraversalDistance(float distance) {
        this.distance = distance;
    }

    public float getTraversalDistance() {
        return distance;
    }

    public void setSourceAntenna(BlockPos sourceAntennaPos) {
        this.sourceAntennaPos = sourceAntennaPos;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        if (obj instanceof Signal signal) {
            return getEncodedData() != null && getEncodedData().equals(signal.getEncodedData());
        }
        return false;
    }

    /**
     * Wraps this signal into a new list.
     * @return A new list containing this signal.
     */
    public List<Signal> wrap() {
        ArrayList<Signal> signals = new ArrayList<>();
        signals.add(this);
        return signals;
    }

    @Override
    public String toString() {
        return "Signal Frequency: " + this.frequency + " Amplitude: " + this.amplitude;
    }
}
