package org.modogthedev.superposition.fabric.util;

import net.fabricmc.loader.api.FabricLoader;
import org.modogthedev.superposition.compat.CompatabilityHandler;
import org.modogthedev.superposition.fabric.compat.cc.SuperpositionFabricComputerCraftCompatibility;

public class FabricCompatibility {
    public static void setup() {
        for (CompatabilityHandler.Mods mods : CompatabilityHandler.Mods.values()) {
            if (FabricLoader.getInstance().isModLoaded(mods.name().toLowerCase())) {
                mods.isLoaded = true;
            }
        }
        setupMods();
    }

    public static void setupMods() {
        if (CompatabilityHandler.Mods.COMPUTERCRAFT.isLoaded) {
            SuperpositionFabricComputerCraftCompatibility.setup();
        }
    }
}
