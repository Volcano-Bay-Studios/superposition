package org.modogthedev.superposition.forge;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.modogthedev.superposition.Superposition;

@EventBusSubscriber(modid = Superposition.MODID)
public class SuperpositionForgeEvents {

    @SubscribeEvent
    public static void onClientTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            Superposition.tick(level);
        }
    }
}
