package org.modogthedev.superposition.event;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.modogthedev.superposition.screens.SignalGeneratorScreen;

public class ClientEvents {
    public static int ticks = 0;
    public static void clientTickEvent(TickEvent.ClientTickEvent event) {

    }
}
