package org.modogthedev.superposition.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.SuperpositionClient;
import org.modogthedev.superposition.system.sound.SpeakerSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

@Mixin(SoundEngine.class)
public class FabricSoundEngineMixin {
    @Nullable
    @Unique
    private static SoundEngine self;

    @Inject(method = "play", at = @At(value = "HEAD"))
    @SuppressWarnings("unused")
    private void playSound(SoundInstance sound, CallbackInfo ci) {
        self = (SoundEngine) (Object) this;
    }

    @Inject(at = @At("TAIL"), method = "method_19755")
    @SuppressWarnings("unused")
    private static void onStream(AudioStream audioStream, Channel channel, CallbackInfo ci) {
        SuperpositionClient.playStreaming((self), channel, audioStream);
    }

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
