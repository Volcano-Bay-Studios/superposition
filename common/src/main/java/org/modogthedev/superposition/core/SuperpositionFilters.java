package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.resources.ResourceKey;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.filter.BandPassFilter;
import org.modogthedev.superposition.system.filter.Filter;
import org.modogthedev.superposition.system.filter.HighPassFilter;
import org.modogthedev.superposition.system.filter.LowPassFilter;

import java.util.function.Supplier;

public class SuperpositionFilters {

    public static final RegistrationProvider<Filter> FILTERS = RegistrationProvider.get(ResourceKey.createRegistryKey(Superposition.id("filter")), Superposition.MODID);
    public static final RegistryObject<Filter> HIGH_PASS = registerFilter("high_pass", () -> new HighPassFilter(Superposition.id("high_pass")));
    public static final RegistryObject<Filter> LOW_PASS = registerFilter("low_pass", () -> new LowPassFilter(Superposition.id("low_pass")));
    public static final RegistryObject<Filter> BAND_PASS = registerFilter("band_pass", () -> new BandPassFilter(Superposition.id("band_pass")));

    private static <T extends Filter> RegistryObject<T> registerFilter(String name, Supplier<T> filter) {
        return FILTERS.register(name, filter);
    }

    public static void bootstrap() {
    }
}
