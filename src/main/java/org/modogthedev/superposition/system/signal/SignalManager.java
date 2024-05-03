package org.modogthedev.superposition.system.signal;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignalManager {
    static HashMap<Level, List<Signal>> transmittedSignals = new HashMap<>();
    static HashMap<Level, List<Signal>> untransmittedSignals = new HashMap<>();

    public static void tick(TickEvent.LevelTickEvent event) {
        Level level = event.level;
        ifAbsent(level);
        if (!level.isClientSide) {
            for (Signal signal : transmittedSignals.get(level)) {
                signal.tick();
            }
        }
    }

    private static void ifAbsent(Level level) {
        if (!transmittedSignals.containsKey(level)) {
            transmittedSignals.put(level, new ArrayList<>());
            untransmittedSignals.put(level, new ArrayList<>());
        }
    }

    public static void addParticle(Vec3 pos, Level level, float frequency, float quanta, Vec3 vel) {
        ifAbsent(level);
        transmittedSignals.get(level).add(new Signal(pos, level, frequency, quanta, vel));
    }
}
