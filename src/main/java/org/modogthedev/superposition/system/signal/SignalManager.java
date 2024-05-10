package org.modogthedev.superposition.system.signal;

import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignalManager {
    public static HashMap<Level, List<Signal>> transmittedSignals = new HashMap<>();
    static HashMap<Level, List<Signal>> untransmittedSignals = new HashMap<>();

    public static void tick(TickEvent.LevelTickEvent event) {
        Level level = event.level;
        ifAbsent(level);
        if (!level.isClientSide) {
            AntennaManager.clearSignals(level);
            List<Signal> signalsForRemoval = new ArrayList<>();
            for (Signal signal : transmittedSignals.get(level)) {
                AntennaManager.postSignal(signal);
                if (signal.tick()) {
                      signalsForRemoval.add(signal);
                }
            }
            transmittedSignals.get(level).removeAll(signalsForRemoval);
        }
    }
    public static void postSignalsToAntenna(Antenna antenna){
        antenna.signals.clear();
        for (Signal signal : transmittedSignals.get(antenna.level)) {
            AntennaManager.postSignalToAntenna(signal,antenna);
        }
    }

    private static void ifAbsent(Level level) {
        if (!transmittedSignals.containsKey(level)) {
            transmittedSignals.put(level, new ArrayList<>());
            untransmittedSignals.put(level, new ArrayList<>());
        }
    }

    public static void addSignal(Signal signal) {
        if (signal.level.isClientSide)
            return;
        ifAbsent(signal.level);
        if (transmittedSignals.get(signal.level).contains(signal)) {
            transmittedSignals.get(signal.level).set(transmittedSignals.get(signal.level).indexOf(signal),signal);
        } else {
            transmittedSignals.get(signal.level).add(signal);
        }
    }
    public static void stopSignal(Signal signal){
        if (transmittedSignals.get(signal.level).contains(signal)) {
            Signal ourSignal = transmittedSignals.get(signal.level).get(transmittedSignals.get(signal.level).indexOf(signal));
            ourSignal.emitting = false;
            transmittedSignals.get(signal.level).set(transmittedSignals.get(signal.level).indexOf(signal),ourSignal);
        }
    }
}
