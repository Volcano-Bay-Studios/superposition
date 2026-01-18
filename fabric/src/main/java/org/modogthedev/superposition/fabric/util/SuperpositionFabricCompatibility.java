package org.modogthedev.superposition.fabric.util;

import net.fabricmc.loader.api.FabricLoader;
import org.modogthedev.superposition.compat.CompatabilityHandler;
import org.modogthedev.superposition.fabric.compat.cc.SuperpositionFabricComputerCraftCompatibility;

public class SuperpositionFabricCompatibility {
    public static void setup() {
        for (CompatabilityHandler.Mod mod : CompatabilityHandler.Mod.values()) {
            if (FabricLoader.getInstance().isModLoaded(mod.name().toLowerCase())) {
                mod.isLoaded = true;
            }
        }
        setupMods();
    }

    public static void setupMods() {
        if (CompatabilityHandler.Mod.COMPUTERCRAFT.isLoaded) {
            SuperpositionFabricComputerCraftCompatibility.setup();
        }
    }
}
