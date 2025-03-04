package org.modogthedev.superposition.util;

import net.minecraft.client.resources.sounds.TickableSoundInstance;

public interface IMusicInstance extends TickableSoundInstance {
    default void setVolume(float data) {
    }
}
