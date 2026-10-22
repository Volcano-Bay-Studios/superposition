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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SpeakerSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {


    private final AudioStream stream;
    private final Sound streamSound;
    private boolean stopped = false;
    public static final ResourceLocation STREAM = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("superposition", "empty");

    public static final Sound EMPTY = new Sound(
            STREAM, ConstantFloat.of(1.0F), ConstantFloat.of(1.0F), 1, Sound.Type.FILE, true, false, 16
    );

    protected SpeakerSoundInstance(AudioStream stream) {
        super(STREAM, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.stream = stream;
        this.streamSound = EMPTY;
    }

    protected SpeakerSoundInstance(AudioStream stream, int range) {
        super(STREAM, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.stream = stream;
        this.streamSound = new Sound(
                STREAM, ConstantFloat.of(1.0F), ConstantFloat.of(1.0F), 1, Sound.Type.FILE, true, false, range
        );
    }

    public CompletableFuture<AudioStream> getSoundStream(SoundBufferLibrary soundBuffers, ResourceLocation sound, boolean looping) {
        return CompletableFuture.completedFuture(stream);
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    public void stop() {
        this.stopped = true;
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    /**
     * @param pos The position to play from in the world, or null to play at the listener
     */
    public void setPosition(@Nullable Vec3 pos) {
        this.relative = pos == null;
        this.attenuation = pos == null ? Attenuation.NONE : Attenuation.LINEAR;
        this.x = pos == null ? 0 : pos.x;
        this.y = pos == null ? 0 : pos.y;
        this.z = pos == null ? 0 : pos.z;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public Sound getSound() {
        return streamSound;
    }

    public AudioStream getStream() {
        return stream;
    }
}
