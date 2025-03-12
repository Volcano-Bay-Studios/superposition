package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import org.modogthedev.superposition.Superposition;

import java.util.HashMap;

public class SuperpositionSounds {

    public static final RegistrationProvider<SoundEvent> SOUNDS = RegistrationProvider.get(Registries.SOUND_EVENT, Superposition.MODID);
    
    private static final HashMap<String,RegistryObject<SoundEvent>> songs = new HashMap<>();

    public static final RegistryObject<SoundEvent> EMPTY = registerSoundEvent("empty");
    public static final RegistryObject<SoundEvent> SCREWDRIVER = registerSoundEvent("screwdriver");
    public static final RegistryObject<SoundEvent> DOWN = registerSoundEvent("down");
    public static final RegistryObject<SoundEvent> SWITCH_ON = registerSoundEvent("switch_on");
    public static final RegistryObject<SoundEvent> SWITCH_OFF = registerSoundEvent("switch_off");
    public static final RegistryObject<SoundEvent> SCROLL = registerSoundEvent("scroll");

    // Songs
    public static final RegistryObject<SoundEvent> TRAVELERS = registerSong("travelers","travelers");
    public static final RegistryObject<SoundEvent> TIMBER = registerSong("timber_hearth","timber_hearth");


    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUNDS.register(Superposition.id(name), () -> SoundEvent.createVariableRangeEvent(Superposition.id(name)));
    }

    private static RegistryObject<SoundEvent> registerSong(String name, String key) {
        RegistryObject<SoundEvent> soundEventRegistryObject = registerSoundEvent(name);
        songs.put(key,soundEventRegistryObject);
        return soundEventRegistryObject;
    }

    public static SoundEvent getSong(String soundName) {
        if (songs.containsKey(soundName)) {
            return songs.get(soundName).get();
        }
        return TRAVELERS.get();
    }

    public static void bootstrap() {
    }
}
