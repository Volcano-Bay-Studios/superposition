package org.modogthedev.superposition.forge;

import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.modogthedev.superposition.Superposition;

@EventBusSubscriber(modid = Superposition.MODID, value = Dist.CLIENT)
public class SuperpositionForgeClientEvents {

    @SubscribeEvent
    public static void onClientTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        Superposition.clientTick(level);
    }
}
