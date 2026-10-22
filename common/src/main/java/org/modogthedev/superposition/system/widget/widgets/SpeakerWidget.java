package org.modogthedev.superposition.system.widget.widgets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;
import org.modogthedev.superposition.system.sound.ClientAudioManager;
import org.modogthedev.superposition.system.stream.StreamManager;
import org.modogthedev.superposition.system.widget.Widget;
import org.modogthedev.superposition.system.widget.WidgetHost;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.Objects;
import java.util.UUID;

public class SpeakerWidget extends Widget {
    private UUID stream = null;
    private int delay = 0;
    private float volume = 0;

    @Override
    public boolean tick(Level level, WidgetHost host, int index) {
        Vec3 pos = host.getWidgetPosition();
        if (level.isClientSide) {
            if (stream != null && volume > 0) {
                ClientAudioManager.voices.add(new ClientAudioManager.StreamRequest(getUuid(), stream, delay, volume, pos));
            }
            return super.tick(level, host, index);
        }

        UUID newStream = null;
        int newDelay = 0;
        float newVolume = 0;
        for (Signal signal : getPortSignals("audio", host)) {
            if (signal.getEncodedData() instanceof EncodedData.StreamData streamData) {
                newStream = streamData.id();
                newDelay = streamData.delay();
                newVolume = Math.round(SuperpositionMth.amplitudeToVolume(signal.getAmplitude()) * 32) / 32f;
                break;
            }
        }
        boolean changed = !Objects.equals(stream, newStream) || delay != newDelay || volume != newVolume;
        stream = newStream;
        delay = newDelay;
        volume = newVolume;

        if (stream != null) {
            StreamManager.markSink(level, stream, getUuid(), pos);
        }
        return changed;
    }

    public boolean isPlaying() {
        return stream != null && volume > 0;
    }

    @Override
    public PortConfig.Builder buildPorts(PortConfig.Builder builder) {
        return super.buildPorts(builder).addInputPort("audio");
    }

    @Override
    public void write(CompoundTag tag) {
        super.write(tag);
        if (stream != null) {
            tag.putUUID("stream", stream);
        }
        tag.putInt("delay", delay);
        tag.putFloat("volume", volume);
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);
        stream = tag.hasUUID("stream") ? tag.getUUID("stream") : null;
        if (tag.contains("delay")) {
            delay = tag.getInt("delay");
        }
        if (tag.contains("volume")) {
            volume = tag.getFloat("volume");
        }
    }

    @Override
    public Vector3f getBounds() {
        return new Vector3f(7 / 16f, 1 / 16f, 7 / 16f);
    }
}
