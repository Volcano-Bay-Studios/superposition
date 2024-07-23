package org.modogthedev.superposition.core;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import org.modogthedev.superposition.Superposition;

public class SuperpositionSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Superposition.MODID, Registries.SOUND_EVENT);
    public static final RegistrySupplier<SoundEvent> SINE = registerSoundEvent("sine");
    public static final RegistrySupplier<SoundEvent> SCREWDRIVER = registerSoundEvent("screwdriver");
    public static final RegistrySupplier<SoundEvent> SWITCH_ON = registerSoundEvent("switch_on");
    public static final RegistrySupplier<SoundEvent> SWITCH_OFF = registerSoundEvent("switch_off");
    public static final RegistrySupplier<SoundEvent> SCROLL = registerSoundEvent("scroll");

    private static RegistrySupplier<SoundEvent> registerSoundEvent(String name) {
        return SOUNDS.register(Superposition.id(name), () -> SoundEvent.createVariableRangeEvent(Superposition.id(name)));
    }
}
