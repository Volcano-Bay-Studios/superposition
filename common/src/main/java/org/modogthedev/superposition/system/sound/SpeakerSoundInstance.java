package org.modogthedev.superposition.system.sound;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.ConstantFloat;

import java.util.concurrent.CompletableFuture;

public class SpeakerSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {


    private final AudioStream stream;
    public static final ResourceLocation STREAM = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("superposition", "empty");

    public static final Sound EMPTY = new Sound(
            STREAM, ConstantFloat.of(1.0F), ConstantFloat.of(1.0F), 1, Sound.Type.FILE, true, false, 16
    );

    protected SpeakerSoundInstance(AudioStream stream) {
        super(STREAM, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.stream = stream;
    }

    public CompletableFuture<AudioStream> getSoundStream(SoundBufferLibrary soundBuffers, ResourceLocation sound, boolean looping) {
        return CompletableFuture.completedFuture(stream);
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean isStopped() {
        return false;
    }

    @Override
    public float getVolume() {
        return 1f;
    }

    @Override
    public Sound getSound() {
        return EMPTY;
    }

    public AudioStream getStream() {
        return stream;
    }
}
