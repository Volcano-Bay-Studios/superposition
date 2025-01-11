package org.modogthedev.superposition.system.signal;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaManager;

import java.util.*;

public class ClientSignalManager {
    public static Map<Level, Map<UUID, Signal>> clientSignals = new HashMap<>();

    public static void tick(Level level) {
        if (level == null || !level.isClientSide) {
            return;
        }
        ifAbsent(level);
        AntennaManager.clearSignals(level);
        List<Signal> signalsForRemoval = new ArrayList<>();
        for (Signal signal : clientSignals.get(level).values()) {
            if (signal.tick()) {
                signalsForRemoval.add(signal);
            }
        }
        clientSignals.get(level).values().removeAll(signalsForRemoval);
    }

    public static void processTag(Level level, FriendlyByteBuf buf) {
        ifAbsent(level);
        Map<UUID, Signal> signalMap = clientSignals.get(level);
        Set<UUID> removed = new HashSet<>(signalMap.keySet());
        int count = buf.readVarInt();
        for (int i = 0; i < count; i++) {
            UUID id = buf.readUUID();
            if (removed.remove(id)) {
                signalMap.get(id).load(id, buf);
            } else {
                Signal signal = new Signal(id, buf);
                signal.level = level;
                signalMap.put(id, signal);
            }
        }
        signalMap.keySet().removeAll(removed);
    }

    public static void postSignalsToAntenna(Antenna antenna) {
        for (Signal signal : clientSignals.get(antenna.level).values()) {
            AntennaManager.postSignalToAntenna(signal, antenna);
        }
    }

    public static void stopSignal(Signal signal) {
        if (clientSignals.get(signal.level).containsValue(signal) && signal.isEmitting()) {
            signal.stop();
        }
    }

    private static void ifAbsent(Level level) {
        if (!clientSignals.containsKey(level)) {
            clientSignals.put(level, new HashMap<>());
        }
    }
}
