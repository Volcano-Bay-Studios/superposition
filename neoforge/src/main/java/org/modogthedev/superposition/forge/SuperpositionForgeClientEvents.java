package org.modogthedev.superposition.forge;

import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.CarabinerManager;
import org.modogthedev.superposition.system.signal.ClientSignalManager;

@EventBusSubscriber(modid = Superposition.MODID, value = Dist.CLIENT)
public class SuperpositionForgeClientEvents {

    @SubscribeEvent
    public static void onClientTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        ClientSignalManager.tick(level);
        CableManager.clientTick(level);
        CarabinerManager.tick(level);
    }

    @SubscribeEvent
    public static void onEmptyClick(PlayerInteractEvent.RightClickEmpty event) {
        Level level = event.getLevel();
        ClientSignalManager.tick(level);
        CableManager.clientTick(level);
        CarabinerManager.tick(level);
    }
}
