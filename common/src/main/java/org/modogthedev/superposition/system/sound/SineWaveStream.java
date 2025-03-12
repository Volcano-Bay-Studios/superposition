package org.modogthedev.superposition.system.sound;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.sounds.AudioStream;
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

public class SineWaveStream implements AudioStream {
    ByteArrayInputStream byteArrayInputStream;

    public SineWaveStream(byte[] bytes) {
        byteArrayInputStream = new ByteArrayInputStream(bytes);
    }

    public Channel channel;
    public Executor executor;

    @Override
    public AudioFormat getFormat() {
        return ClientAudioManager.SINE_FORMAT;
    }

    @Override
    public ByteBuffer read(int i) throws IOException {
//        return ByteBuffer.wrap(byteArrayInputStream.readAllBytes());
        ByteBuffer dataBuffer = BufferUtils.createByteBuffer(i);
        while (dataBuffer.hasRemaining()) {
            dataBuffer.put((byte) byteArrayInputStream.read());
        }
        dataBuffer.flip();
        return dataBuffer;
    }

    @Override
    public void close() throws IOException {

    }
}
