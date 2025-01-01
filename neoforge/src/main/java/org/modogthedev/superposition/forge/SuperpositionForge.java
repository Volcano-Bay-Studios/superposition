package org.modogthedev.superposition.forge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.modogthedev.superposition.Superposition;

@Mod(Superposition.MODID)
public class SuperpositionForge {

    public SuperpositionForge(IEventBus modEventBus) {
        SuperpositionForgeCompatability.setup();
        Superposition.init();
    }

}
