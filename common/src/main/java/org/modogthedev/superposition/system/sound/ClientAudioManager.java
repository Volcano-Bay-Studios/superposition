package org.modogthedev.superposition.system.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.lwjgl.BufferUtils;
import org.modogthedev.superposition.core.SuperpositionSounds;
import org.modogthedev.superposition.util.IMusicInstance;
import org.modogthedev.superposition.util.SimpleMusicInstance;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;

public class ClientAudioManager {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static float volume = 0f;

    private static IMusicInstance currentMusic;
    private static final int sampleRate = 44100;

    public static final AudioFormat SINE_FORMAT = new AudioFormat(sampleRate, 8, 1, true, false);

    public static void tick(Level level) {
        if (volume == 0) {
            minecraft.getSoundManager().stop(currentMusic);
            currentMusic = null;
            return;
        }

        if (currentMusic == null) {
            currentMusic = SimpleMusicInstance.forMusic(SuperpositionSounds.TRAVELERS.get());
            minecraft.getSoundManager().play(currentMusic);
        }
        if (volume <= 0) {
            currentMusic.setVolume(0);
        } else {
            currentMusic.setVolume((float) (Math.log10(volume) + 1.3f) / 3f);
        }
        volume = 0;
    }

    public static void addVolume(float input) {
        volume += input;
    }

    public static void playSine(float frequency) {
        byte[] sineWaveData = generateSineWave(frequency, sampleRate, 1);
        minecraft.getSoundManager().play(new SpeakerSoundInstance(new SineWaveStream(sineWaveData)));
    }

    public static void setup() {
    }

    public static void playBufffer(byte[] bytes, int sampleRate) {
        int duration = 2; // seconds
        int numSamples = sampleRate * duration;

        ByteBuffer dataBuffer = BufferUtils.createByteBuffer(bytes.length);
        dataBuffer.put(bytes);
        dataBuffer.flip();

    }

    public static void dispose() {
    }

    private static byte[] generateSineWave(double frequency, int sampleRate, float duration) {
        int numSamples = (int) (sampleRate * duration);
        byte[] data = new byte[numSamples];
        for (int i = 0; i < numSamples; i++) {
            double time = (double) i / sampleRate;
            double amplitude = Math.sin(2 * Math.PI * frequency * time);
            data[i] = (byte) (amplitude * 127); // Scale to byte range (-127 to 127)
        }
        return data;
    }
}
