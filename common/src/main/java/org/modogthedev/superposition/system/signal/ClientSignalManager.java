package org.modogthedev.superposition.system.signal;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
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
                Signal signal = new Signal(level,id, buf);
                signal.level = level;
                signalMap.put(id, signal);
            }
        }
        signalMap.keySet().removeAll(removed);
    }

    public static void processBlockBoundTag(Level level, FriendlyByteBuf buf) {
        ifAbsent(level);
        BlockPos pos = buf.readBlockPos();
        if (level.getBlockEntity(pos) instanceof SignalActorBlockEntity signalActorBlockEntity) {
            int count = buf.readVarInt();
            List<Signal> signals = signalActorBlockEntity.getSignals();
            for (int i = 0; i < Math.min(count,signals.size()); i++) {
                UUID id = buf.readUUID();
                signals.get(i).load(id,buf);
            }
            if (signals.size() > count) {
                for (int i = signals.size(); i > count; i--) {
                    signals.remove(i-1);
                }
            }
            if (signals.size() < count) {
                for (int i = signals.size(); i < count; i++) {
                    UUID id = buf.readUUID();
                    Signal signal = new Signal(level,id, buf);
                    signal.level = level;
                    signals.add(signal);
                }
            }
        }
    }

    public static void stopSignal(Signal signal) {
        if (clientSignals.get(signal.level).containsValue(signal) && signal.isEmitting()) {
            signal.stop();
        }
    }

    public static void ifAbsent(Level level) {
        if (!clientSignals.containsKey(level)) {
            clientSignals.put(level, new HashMap<>());
        }
    }
}
