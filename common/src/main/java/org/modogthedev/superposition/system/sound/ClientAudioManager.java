package org.modogthedev.superposition.system.sound;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.level.Level;
import org.lwjgl.BufferUtils;
import org.modogthedev.superposition.core.SuperpositionSounds;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.IMusicInstance;
import org.modogthedev.superposition.util.SimpleMusicInstance;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientAudioManager {
    private static final Minecraft minecraft = Minecraft.getInstance();

    private static final int sampleRate = 44100;
    private static final HashMap<String, IMusicInstance> tracks = new HashMap<>();

    public static List<Signal> signals = new ArrayList<>();
    public static List<Signal> oldSignals = new ArrayList<>();

    public static final AudioFormat SINE_FORMAT = new AudioFormat(sampleRate, 8, 1, true, false);
    public static SpeakerSoundInstance speakerSoundInstance;

    public static int ticks = 0;

    public static void tick(Level level) { // TODO: play all sounds individually
        SoundManager soundManager = minecraft.getSoundManager();
        List<String> oldTracks = new ArrayList<>(tracks.keySet());
        for (Signal signal : signals) {
            if (signal != null && signal.getEncodedData() != null && signal.getAmplitude() > 0) {
                String trackName = signal.getEncodedData().stringValue();
                if (trackName != null) {
                    oldTracks.remove(trackName);
                    if (tracks.containsKey(trackName)) {
                        tracks.get(trackName).setVolume(signal.getAmplitude());
                    } else {
                        IMusicInstance musicInstance = SimpleMusicInstance.forMusic(SuperpositionSounds.getSong(trackName));
                        tracks.put(trackName, musicInstance);
                        musicInstance.setVolume(signal.getAmplitude());
                        soundManager.play(musicInstance);
                    }
                }
            }
        }
        for (String trackName : oldTracks) {
            soundManager.stop(tracks.get(trackName));
            tracks.remove(trackName);
        }
        oldSignals.clear();
        oldSignals.addAll(signals);
        signals.clear();
        // TODO: stop sound when not looking

        if (speakerSoundInstance != null && !soundManager.isActive(speakerSoundInstance)) {
            soundManager.stop(speakerSoundInstance);
            speakerSoundInstance = null;
        }

        if (speakerSoundInstance != null) {
            if (!(speakerSoundInstance.getStream() instanceof SineWaveStream sineWaveStream)) return;
            if (sineWaveStream.channel == null) return;
            sineWaveStream.channel.attachBufferStream(sineWaveStream);
            sineWaveStream.channel.play();
        }
    }

    public static void playSine(float frequency) {
        SoundManager soundManager = minecraft.getSoundManager();
        if (speakerSoundInstance != null && !soundManager.isActive(speakerSoundInstance)) {
            soundManager.stop(speakerSoundInstance);
            speakerSoundInstance = null;
        }
        if (speakerSoundInstance == null) {
            byte[] sineWaveData = generateSineWave(frequency, sampleRate, 1);
            speakerSoundInstance = new SpeakerSoundInstance(new SineWaveStream(sineWaveData));
            soundManager.play(speakerSoundInstance);


        }
    }

    public static void stopSine() {
        SoundManager soundManager = minecraft.getSoundManager();
        if (speakerSoundInstance != null) {
            soundManager.stop(speakerSoundInstance);
            speakerSoundInstance = null;
        }
    }

    public static void setup() {
    }

    public static void playStreaming(SoundEngine soundEngine, Channel channel, AudioStream stream) {
        if (!(stream instanceof SineWaveStream sineWaveStream)) return;

        sineWaveStream.channel = channel;
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
