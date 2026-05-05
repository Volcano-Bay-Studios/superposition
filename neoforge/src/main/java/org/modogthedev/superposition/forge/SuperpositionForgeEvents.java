package org.modogthedev.superposition.forge;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.compat.CompatabilityHandler;
import org.modogthedev.superposition.core.SuperpositionCommands;
import org.modogthedev.superposition.forge.compat.cc.SuperpositionForgeComputerCraftCompatibility;

@EventBusSubscriber(modid = Superposition.MODID)
public class SuperpositionForgeEvents {

    @SubscribeEvent
    public static void onServerTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            Superposition.tick(level);
        }
    }

    @SubscribeEvent
    public static void onLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            Superposition.loadLevel(level);
        }
    }

    @SubscribeEvent
    public static void onPreTick(LevelTickEvent.Pre event) {
        if (event.getLevel() instanceof ServerLevel level) {
            Superposition.preTick(level);
        }
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(SuperpositionCommands.registerSuperpositionCommand());
    }

    @SubscribeEvent
    public static void attachPeripherals(RegisterCapabilitiesEvent event) {
        if (CompatabilityHandler.Mod.COMPUTERCRAFT.isLoaded) {
            SuperpositionForgeComputerCraftCompatibility.attachPeripherals(event);
        }
    }
}
