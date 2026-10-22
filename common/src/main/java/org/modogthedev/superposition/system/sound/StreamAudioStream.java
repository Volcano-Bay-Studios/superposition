package org.modogthedev.superposition.system.sound;

import io.github.jaredmdobson.concentus.OpusDecoder;
import io.github.jaredmdobson.concentus.OpusException;
import net.minecraft.client.sounds.AudioStream;
import org.lwjgl.BufferUtils;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.stream.ClientStreamManager;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StreamAudioStream implements AudioStream {
    private static final int CHUNK_FRAMES = 2;
    private static final int START_FRAMES = 3;
    private static final int MAX_FRAMES = 10;
    private static final int MAX_CONCEALED_FRAMES = 2;

    private final UUID stream;
    private final ArrayDeque<short[]> queue = new ArrayDeque<>();
    private final List<ClientStreamManager.Frame> due = new ArrayList<>();
    private OpusDecoder decoder;
    private volatile int delay;
    private long lastOrder;
    private int lastSequence = -1;
    private float gain = 1;
    private boolean buffering = true;
    private volatile long lastRead = System.nanoTime();
    private volatile int decoded = 0;
    private volatile int failed = 0;

    public StreamAudioStream(UUID stream, int delay) {
        this.stream = stream;
        this.delay = delay;
        this.lastOrder = ClientStreamManager.collect(stream, delay, -1, due);
        try {
            this.decoder = OpusCodec.createDecoder();
        } catch (OpusException e) {
            Superposition.LOGGER.error("Failed to create audio stream decoder", e);
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public AudioFormat getFormat() {
        return OpusCodec.FORMAT;
    }

    @Override
    public ByteBuffer read(int size) {
        lastRead = System.nanoTime();
        decodeDue();

        ByteBuffer buffer = BufferUtils.createByteBuffer(CHUNK_FRAMES * OpusCodec.FRAME_SIZE * 2).order(ByteOrder.LITTLE_ENDIAN);
        ShortBuffer samples = buffer.asShortBuffer();
        if (queue.isEmpty()) {
            buffering = true;
        } else if (queue.size() >= START_FRAMES) {
            buffering = false;
        }
        for (int i = 0; i < CHUNK_FRAMES; i++) {
            short[] frame = buffering ? null : queue.pollFirst();
            if (frame != null) {
                samples.put(frame);
            } else {
                samples.position(samples.position() + OpusCodec.FRAME_SIZE);
            }
        }
        return buffer;
    }

    private void decodeDue() {
        if (decoder == null) {
            return;
        }
        due.clear();
        lastOrder = ClientStreamManager.collect(stream, delay, lastOrder, due);
        for (ClientStreamManager.Frame frame : due) {
            int missing = lastSequence < 0 ? 0 : frame.sequence() - lastSequence - 1;
            if (missing > 0 && missing <= MAX_CONCEALED_FRAMES) {
                for (int i = 0; i < missing; i++) {
                    decode(null);
                }
            }
            lastSequence = frame.sequence();
            gain = frame.gain();
            decode(frame.data());
        }
        while (queue.size() > MAX_FRAMES) {
            queue.removeFirst();
        }
    }

    /**
     * @param data The frame to decode, or null to conceal a lost frame
     */
    private void decode(byte[] data) {
        short[] samples = new short[OpusCodec.FRAME_SIZE];
        try {
            decoder.decode(data, 0, data == null ? 0 : data.length, samples, 0, OpusCodec.FRAME_SIZE, false);
            for (int i = 0; i < samples.length; i++) {
                samples[i] = (short) (samples[i] * gain);
            }
            queue.addLast(samples);
            decoded++;
        } catch (OpusException | RuntimeException e) {
            failed++;
        }
    }

    public void resetStall() {
        lastRead = System.nanoTime();
    }

    /**
     * @return The milliseconds since the sound engine last read from this, which is large if it has stopped playing it
     */
    public long getStall() {
        return (System.nanoTime() - lastRead) / 1000000;
    }

    public String getDebugInfo() {
        return decoded + " frames decoded, " + failed + " failed, last read " + getStall() + "ms ago";
    }

    @Override
    public void close() {
    }
}
