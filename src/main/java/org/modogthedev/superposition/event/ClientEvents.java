package org.modogthedev.superposition.event;

import net.minecraftforge.event.TickEvent;
import org.modogthedev.superposition.screens.SignalGeneratorScreen;

public class ClientEvents {
    public static int ticks = 0;
    public static void clientTickEvent(TickEvent.ClientTickEvent event) {
        SignalGeneratorScreen.ticks++;
    }
}
