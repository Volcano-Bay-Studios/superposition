package org.modogthedev.superposition.system.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.util.Mth;
import org.modogthedev.superposition.core.SuperpositionConstants;

import java.util.UUID;

/**
 * A stream that is currently being played to the player.
 */
public class StreamVoice {
    // The milliseconds the sound engine can go without reading the stream before it is considered dropped
    private static final int STALL_TIME = 500;

    private final UUID stream;
    private final StreamAudioStream audio;
    private SpeakerSoundInstance soundInstance;

    public StreamVoice(ClientAudioManager.StreamRequest request) {
        this.stream = request.stream();
        this.audio = new StreamAudioStream(request.stream(), request.delay());
    }

    public UUID getStream() {
        return stream;
    }

    public void update(ClientAudioManager.StreamRequest request, SoundManager soundManager) {
        audio.setDelay(request.delay());
        // The sound engine drops the sound if its channel ever runs dry, so it is started again
        boolean stalled = audio.getStall() > STALL_TIME && !Minecraft.getInstance().isPaused();
        boolean restart = soundInstance == null || stalled || !soundManager.isActive(soundInstance);
        if (restart) {
            stop(soundManager);
            audio.resetStall();
            soundInstance = new SpeakerSoundInstance(audio, (int) SuperpositionConstants.speakerRange);
        }
        soundInstance.setVolume(Mth.clamp(request.volume(), 0, 1));
        soundInstance.setPosition(request.position());
        if (restart) {
            soundManager.play(soundInstance);
        }
    }

    public String getDebugInfo() {
        return stream + ": " + audio.getDebugInfo();
    }

    public void stop(SoundManager soundManager) {
        if (soundInstance != null) {
            soundInstance.stop();
            soundManager.stop(soundInstance);
            soundInstance = null;
        }
    }
}
