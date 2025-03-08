package org.modogthedev.superposition.forge;

import org.modogthedev.superposition.compat.CompatabilityHandler;

public class SuperpositionForgeCompatability {
    public static void setup() {
        for (CompatabilityHandler.Mod mod : CompatabilityHandler.Mod.values()) {
            if (true) { // TODO: add neoforge mod checking
                mod.isLoaded = true;
            }
        }
    }
}
