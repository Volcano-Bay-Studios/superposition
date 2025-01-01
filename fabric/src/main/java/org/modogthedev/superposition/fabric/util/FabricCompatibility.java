package org.modogthedev.superposition.fabric.util;

import net.fabricmc.loader.api.FabricLoader;
import org.modogthedev.superposition.compat.CompatabilityHandler;

public class FabricCompatibility {
    public static void setup() {
        for (CompatabilityHandler.Mods mods : CompatabilityHandler.Mods.values()) {
            if (FabricLoader.getInstance().isModLoaded(mods.name().toLowerCase())) {
                mods.isLoaded = true;
            }
        }
    }
}
