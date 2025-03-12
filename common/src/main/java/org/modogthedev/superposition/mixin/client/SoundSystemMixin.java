package org.modogthedev.superposition.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.sound.SpeakerSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

@Mixin(SoundEngine.class)
public class SoundSystemMixin {
    @WrapOperation(
            method = "play",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;getStream(Lnet/minecraft/resources/ResourceLocation;Z)Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private CompletableFuture<?> getStream(SoundBufferLibrary instance, ResourceLocation resourceLocation, boolean isWrapper, Operation<CompletableFuture<AudioStream>> original, SoundInstance soundInstance) {
        if (soundInstance instanceof SpeakerSoundInstance speakerSoundInstance) {
            return speakerSoundInstance.getSoundStream(instance,resourceLocation,isWrapper);
        }
        return original.call(instance,resourceLocation,isWrapper);
    }


}