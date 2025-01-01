package org.modogthedev.superposition.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.fabric.util.FabricCompatibility;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.CablePassthroughManager;
import org.modogthedev.superposition.system.cable.CarabinerManager;
import org.modogthedev.superposition.system.signal.SignalManager;

public class SuperpositionFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricCompatibility.setup();
        Superposition.init();

        ServerTickEvents.END_WORLD_TICK.register(level -> {
            SignalManager.tick(level);
            CableManager.tick(level);
            CablePassthroughManager.tick(level);
            CarabinerManager.tick(level);
        });
    }
}
