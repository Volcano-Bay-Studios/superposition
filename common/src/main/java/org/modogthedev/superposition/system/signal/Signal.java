package org.modogthedev.superposition.system.signal;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.signal.data.EncodedData;

import java.util.UUID;

public class Signal {

    public float frequency;
    public float sourceFrequency;
    public BlockPos sourceAntennaPos;
    public int sourceAntennaSize = 0;
    public static final int speed = 64;
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
    private EncodedData encodedData;
    public EncodedData data() {
        if (encodedData == null)
            encodedData = new EncodedData();
        return encodedData;
    }

    public boolean tick() {
//        for (float i = 0; i < 361; i += .1f) {
//            this.level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.x + (Math.sin(i)*maxDist), pos.y, pos.z+ (Math.cos(i)*maxDist), 0, 0, 0);
//        }
        lifetime++;
        maxRange = amplitude*100;
        if (!emitting) {
            int endTicks = lifetime-endTime-2;
            minDist = endTicks*speed;
            if (minDist > maxRange) {
                return true;
            }
        }
        maxDist = Math.min(maxRange,lifetime*speed);
        return false;
    }

    public Signal(Vec3 pos, Level level, float frequency, float amplitude, float sourceFrequency) {
        this.pos = pos;
        this.level = level;
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.sourceFrequency = sourceFrequency;
    }
    public Signal(FriendlyByteBuf buf) {
        uuid = buf.readUUID();
        pos = new Vec3(buf.readFloat(),buf.readFloat(),buf.readFloat());
        amplitude = buf.readFloat();
        frequency = buf.readFloat();
        sourceFrequency = buf.readFloat();
        modulation = buf.readFloat();
        emitting = buf.readBoolean();
        lifetime = buf.readInt();
        sourceAntennaPos = new BlockPos(buf.readInt(),buf.readInt(),buf.readInt());
        sourceAntennaSize = buf.readInt();
        encodedData = EncodedData.deserialize(buf.readByteArray());
    }
    public void save(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeFloat((float) pos.x);
        buf.writeFloat((float) pos.y);
        buf.writeFloat((float) pos.z);
        buf.writeFloat(amplitude);
        buf.writeFloat(frequency);
        buf.writeFloat(sourceFrequency);
        buf.writeFloat(modulation);
        buf.writeBoolean(emitting);
        buf.writeInt(lifetime);
        buf.writeInt(sourceAntennaPos.getX());
        buf.writeInt(sourceAntennaPos.getY());
        buf.writeInt(sourceAntennaPos.getZ());
        buf.writeInt(sourceAntennaSize);
        buf.writeByteArray(data().encode());
    }
    public void copy(Signal signal) {
        this.modulation = signal.modulation;
        this.emitting = signal.emitting;
        this.lifetime = signal.lifetime;
        this.level = signal.level;
        this.frequency = signal.frequency;
        this.amplitude = signal.amplitude;
        this.pos = signal.pos;
        this.sourceFrequency = signal.sourceFrequency;
        this.sourceAntennaPos = signal.sourceAntennaPos;
        this.sourceAntennaSize = signal.sourceAntennaSize;
        this.encodedData =  signal.encodedData;
    }


    public void setModulation(float newModulation) {
        modulation = newModulation;
    }

    @Override
    public String toString() {
        return "Signal Frequency: "+frequency+" Amplitude: "+amplitude;
    }
}
