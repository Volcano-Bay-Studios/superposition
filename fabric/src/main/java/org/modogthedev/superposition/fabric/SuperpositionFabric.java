package org.modogthedev.superposition.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.fabric.util.FabricCompatibility;

public class SuperpositionFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricCompatibility.setup();
        Superposition.init();

        ServerTickEvents.END_WORLD_TICK.register(Superposition::tick);
        ServerTickEvents.START_WORLD_TICK.register(Superposition::preTick);
    }
}
