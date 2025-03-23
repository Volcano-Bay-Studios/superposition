package org.modogthedev.superposition.forge.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundEngine;
import org.modogthedev.superposition.system.sound.SpeakerSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

@Mixin(SoundEngine.class)
public class ForgeSoundEngineMixin {

    @WrapOperation(
            method = "play",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/sounds/SoundInstance;getStream(Lnet/minecraft/client/sounds/SoundBufferLibrary;Lnet/minecraft/client/resources/sounds/Sound;Z)Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private CompletableFuture<?> getStream(SoundInstance instance, SoundBufferLibrary soundBufferLibrary, Sound sound, boolean b, Operation<CompletableFuture> original) {
        if (instance instanceof SpeakerSoundInstance speakerSoundInstance) {
            return speakerSoundInstance.getSoundStream(soundBufferLibrary,sound.getPath(),b);
        }
        return original.call(instance, soundBufferLibrary, sound, b);
    }
}
