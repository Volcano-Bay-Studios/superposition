package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import org.modogthedev.superposition.Superposition;

public class SuperpositionSounds {

    public static final RegistrationProvider<SoundEvent> SOUNDS = RegistrationProvider.get(Registries.SOUND_EVENT, Superposition.MODID);
    public static final RegistryObject<SoundEvent> SINE = registerSoundEvent("sine");
    public static final RegistryObject<SoundEvent> SCREWDRIVER = registerSoundEvent("screwdriver");
    public static final RegistryObject<SoundEvent> SWITCH_ON = registerSoundEvent("switch_on");
    public static final RegistryObject<SoundEvent> SWITCH_OFF = registerSoundEvent("switch_off");
    public static final RegistryObject<SoundEvent> SCROLL = registerSoundEvent("scroll");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUNDS.register(Superposition.id(name), () -> SoundEvent.createVariableRangeEvent(Superposition.id(name)));
    }

    public static void bootstrap() {
    }
}
