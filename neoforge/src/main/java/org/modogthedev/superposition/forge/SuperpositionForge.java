package org.modogthedev.superposition.forge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.SuperpositionClient;

@Mod(Superposition.MODID)
public class SuperpositionForge {
    public SuperpositionForge(IEventBus modEventBus) {
        Superposition.init();
    }

}
