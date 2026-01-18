package org.modogthedev.superposition.forge;

import net.neoforged.fml.ModList;
import org.modogthedev.superposition.compat.CompatabilityHandler;

public class SuperpositionForgeCompatability {
    public static void setup() {
        for (CompatabilityHandler.Mod mod : CompatabilityHandler.Mod.values()) {
            if (ModList.get().isLoaded(mod.name().toLowerCase())) {
                mod.isLoaded = true;
            }
        }
    }
}
