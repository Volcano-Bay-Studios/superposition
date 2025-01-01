package org.modogthedev.superposition.forge;

import org.modogthedev.superposition.compat.CompatabilityHandler;

public class SuperpositionForgeCompatability {
    public static void setup() {
        for (CompatabilityHandler.Mods mods : CompatabilityHandler.Mods.values()) {
            if (true) { // TODO: add neoforge mod checking
                mods.isLoaded = true;
            }
        }
    }
}
