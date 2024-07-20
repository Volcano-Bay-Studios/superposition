package org.modogthedev.superposition.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.superposition.Superposition;

public class SuperpositionSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Superposition.MODID);

    public static final RegistryObject<SoundEvent> SINE = registerSoundEvents("sine");
    public static final RegistryObject<SoundEvent> SCREWDRIVER = registerSoundEvents("screwdriver");
    public static final RegistryObject<SoundEvent> SWITCH_ON = registerSoundEvents("switch_on");
    public static final RegistryObject<SoundEvent> SWITCH_OFF = registerSoundEvents("switch_off");
    public static final RegistryObject<SoundEvent> SCROLL = registerSoundEvents("scroll");

    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Superposition.MODID, name)));
    }
}
