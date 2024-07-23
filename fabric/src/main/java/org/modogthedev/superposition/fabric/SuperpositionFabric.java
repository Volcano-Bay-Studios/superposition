package org.modogthedev.superposition.fabric;

import net.fabricmc.api.ModInitializer;
import org.modogthedev.superposition.Superposition;

public class SuperpositionFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Superposition.init();
    }
}
