package org.modogthedev.superposition.fabric.mixin.self;

import net.fabricmc.fabric.api.client.sound.v1.FabricSoundInstance;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.modogthedev.superposition.system.sound.SpeakerSoundInstance;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.CompletableFuture;

@Mixin(SpeakerSoundInstance.class)
public abstract class SpeakerSoundInstanceMixin extends AbstractSoundInstance implements FabricSoundInstance {
    protected SpeakerSoundInstanceMixin(SoundEvent soundEvent, SoundSource source, RandomSource random) {
        super(soundEvent, source, random);
    }

    @Override
    public CompletableFuture<AudioStream> getAudioStream(SoundBufferLibrary loader, ResourceLocation id, boolean repeatInstantly) {
        return ((SpeakerSoundInstance)(Object)this).getSoundStream(loader,id,repeatInstantly);
    }

}
