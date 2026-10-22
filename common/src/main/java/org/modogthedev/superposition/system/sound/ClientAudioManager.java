package org.modogthedev.superposition.system.sound;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.core.SuperpositionSounds;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;
import org.modogthedev.superposition.system.stream.ClientStreamManager;
import org.modogthedev.superposition.util.IMusicInstance;
import org.modogthedev.superposition.util.SimpleMusicInstance;
import org.modogthedev.superposition.util.SuperpositionMth;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.util.*;

public class ClientAudioManager {
    private static final Minecraft minecraft = Minecraft.getInstance();

    private static final int sampleRate = 44100;
    private static final HashMap<String, IMusicInstance> tracks = new HashMap<>();

    public static List<Signal> signals = new ArrayList<>();
    public static List<Signal> oldSignals = new ArrayList<>();

    /**
     * The streams to play this tick, these must be added every tick.
     */
    public static List<StreamRequest> voices = new ArrayList<>();
    private static final Map<UUID, StreamVoice> streamVoices = new HashMap<>();

    /**
     * @param key      A unique id for whatever is playing the stream
     * @param delay    How many ticks behind the stream to play
     * @param position The position to play from in the world, or null to play at the listener
     */
    public record StreamRequest(UUID key, UUID stream, int delay, float volume, @Nullable Vec3 position) {
    }

    public static final AudioFormat SINE_FORMAT = new AudioFormat(sampleRate, 8, 1, true, false);
    public static SpeakerSoundInstance speakerSoundInstance;

    public static int ticks = 0;

    public static void tick(Level level) { // TODO: play all sounds individually
        SoundManager soundManager = minecraft.getSoundManager();
        List<String> oldTracks = new ArrayList<>(tracks.keySet());
        for (Signal signal : signals) {
            if (signal != null && signal.getEncodedData() != null && signal.getAmplitude() > 0) {
                if (signal.getEncodedData() instanceof EncodedData.StreamData streamData) {
                    if (minecraft.player != null) {
                        Vec3 pos = minecraft.player.position();
                        int delay = Math.min(SuperpositionConstants.maxStreamDelay, streamData.delay() + (int) (signal.getPos().distance(pos.x, pos.y, pos.z) / Signal.SPEED));
                        voices.add(new StreamRequest(streamData.id(), streamData.id(), delay, SuperpositionMth.amplitudeToVolume(signal.getAmplitude()), null));
                    }
                    continue;
                }
                String trackName = signal.getEncodedData().stringValue();
                if (trackName != null) {
                    oldTracks.remove(trackName);
                    float volume = SuperpositionMth.amplitudeToVolume(signal.getAmplitude());
                    if (tracks.containsKey(trackName)) {
                        tracks.get(trackName).setVolume(volume);
                    } else {
                        IMusicInstance musicInstance = SimpleMusicInstance.forMusic(SuperpositionSounds.getSong(trackName));
                        tracks.put(trackName, musicInstance);
                        musicInstance.setVolume(volume);
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

        tickVoices(soundManager);
        ClientStreamManager.tick();
        ClientMicrophone.tick();
        if (Superposition.DEBUG && ++ticks % 20 == 0) {
            Superposition.LOGGER.info("Audio: {}", ClientMicrophone.getDebugInfo());
            for (StreamVoice voice : streamVoices.values()) {
                Superposition.LOGGER.info("Audio: playing {}", voice.getDebugInfo());
            }
        }

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

    private static void tickVoices(SoundManager soundManager) {
        Map<UUID, StreamRequest> requests = new HashMap<>();
        for (StreamRequest request : voices) {
            if (request.position != null && (minecraft.player == null || minecraft.player.position().distanceTo(request.position) > SuperpositionConstants.speakerRange)) {
                continue;
            }
            requests.merge(request.key, request, (first, second) -> first.volume >= second.volume ? first : second);
        }
        voices.clear();
        if (requests.size() > SuperpositionConstants.maxStreamSubscriptions) {
            List<StreamRequest> loudest = new ArrayList<>(requests.values());
            loudest.sort(Comparator.comparingDouble(StreamRequest::volume).reversed());
            for (StreamRequest request : loudest.subList(SuperpositionConstants.maxStreamSubscriptions, loudest.size())) {
                requests.remove(request.key);
            }
        }

        Iterator<Map.Entry<UUID, StreamVoice>> iterator = streamVoices.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, StreamVoice> entry = iterator.next();
            StreamRequest request = requests.get(entry.getKey());
            if (request == null || !request.stream.equals(entry.getValue().getStream())) {
                entry.getValue().stop(soundManager);
                iterator.remove();
            }
        }

        Set<UUID> wanted = new HashSet<>();
        for (StreamRequest request : requests.values()) {
            wanted.add(request.stream);
            streamVoices.computeIfAbsent(request.key, uuid -> new StreamVoice(request)).update(request, soundManager);
        }
        ClientStreamManager.subscribe(wanted);
    }

    public static void clear() {
        SoundManager soundManager = minecraft.getSoundManager();
        for (StreamVoice voice : streamVoices.values()) {
            voice.stop(soundManager);
        }
        streamVoices.clear();
        voices.clear();
        ClientStreamManager.clear();
        ClientMicrophone.stop();
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
