package org.modogthedev.superposition.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.CarabinerManager;
import org.modogthedev.superposition.system.signal.SignalManager;

public class SuperpositionFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Superposition.init();

        ServerTickEvents.END_WORLD_TICK.register(level -> {
            SignalManager.tick(level);
            CableManager.tick(level);
            CarabinerManager.tick(level);
        });
    }
}
