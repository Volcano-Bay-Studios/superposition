package org.modogthedev.superposition.system.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.core.SuperpositionSounds;
import org.modogthedev.superposition.util.IMusicInstance;
import org.modogthedev.superposition.util.SimpleMusicInstance;

public class ClientMusicManager {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static float volume = 0f;

    private static IMusicInstance currentMusic;

    public static void tick(Level level) {
        if (volume == 0) {
            minecraft.getSoundManager().stop(currentMusic);
            currentMusic = null;
            return;
        }

        if (currentMusic == null) {
            currentMusic = SimpleMusicInstance.forMusic(SuperpositionSounds.TRAVELERS.get());
            minecraft.getSoundManager().play(currentMusic);
        }
        if (volume <= 0) {
            currentMusic.setVolume(0);
        } else {
            currentMusic.setVolume((float) (Math.log10(volume) + 1.3f) / 3f);
        }
        volume = 0;
    }

    public static void addVolume(float input) {
        volume += input;
    }
}
