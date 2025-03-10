package org.modogthedev.superposition.forge.mixin.self;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.modogthedev.superposition.system.sound.SpeakerSoundInstance;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.CompletableFuture;

@Mixin(SpeakerSoundInstance.class)
public abstract class SpeakerSoundInstanceMixin extends AbstractSoundInstance {
    protected SpeakerSoundInstanceMixin(SoundEvent soundEvent, SoundSource source, RandomSource random) {
        super(soundEvent, source, random);
    }

    @Override
    public @NotNull CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
        return ((SpeakerSoundInstance)(Object)this).getSoundStream(soundBuffers, sound.getPath(), looping);
    }
}
