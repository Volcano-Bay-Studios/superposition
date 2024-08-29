package org.modogthedev.superposition.core;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.filter.BandPassFilter;
import org.modogthedev.superposition.system.filter.Filter;
import org.modogthedev.superposition.system.filter.HighPassFilter;
import org.modogthedev.superposition.system.filter.LowPassFilter;

import java.util.function.Supplier;

public class SuperpositionFilters {
    public static final DeferredRegister<Filter> FILTERS = DeferredRegister.create(Superposition.MODID, SuperpositionRegistries.FILTER_REGISTRY);
    public static final RegistrySupplier<Filter> HIGH_PASS = registerFilter("high_pass", () -> new HighPassFilter(Superposition.id("high_pass")));
    public static final RegistrySupplier<Filter> LOW_PASS = registerFilter("low_pass", () -> new LowPassFilter(Superposition.id("low_pass")));
    public static final RegistrySupplier<Filter> BAND_PASS = registerFilter("band_pass", () -> new BandPassFilter(Superposition.id("band_pass")));

    private static <T extends Filter> RegistrySupplier<T> registerFilter(String name, Supplier<T> filter) {
        return FILTERS.register(name, filter);
    }
}
