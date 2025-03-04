package org.modogthedev.superposition.util;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class SimpleMusicInstance extends AbstractMusicInstance {
    public static SimpleMusicInstance forMusic(SoundEvent p_119746_) {
        return new SimpleMusicInstance(p_119746_, 1.0F, 1.0F, SoundInstance.createUnseededRandom());
    }

    private SimpleMusicInstance(SoundEvent soundEvent, float p_235089_, float p_235090_, RandomSource p_235091_) {
        this(soundEvent, p_235089_, p_235090_, p_235091_, true);
    }

    public SimpleMusicInstance(SoundEvent soundEvent, float volume, float pitch, RandomSource p_235091_, boolean p_235098_) {
        super(soundEvent, SoundSource.BLOCKS, p_235091_);
        this.volume = volume;
        this.pitch = pitch;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.delay = 0;
        this.looping = true;
        this.attenuation = IMusicInstance.Attenuation.NONE;
        this.relative = false;
    }
}