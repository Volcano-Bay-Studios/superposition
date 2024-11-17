package org.modogthedev.superposition.system.signal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaManager;
import org.modogthedev.superposition.util.MapHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ClientSignalManager {
    public static HashMap<Level, HashMap<UUID, Signal>> clientSignals = new HashMap<>();

    public static void tick(Level level) {
        if (level == null || !level.isClientSide)
            return;
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

    public static void processTag(List<Signal> signals) {
        Level level = Minecraft.getInstance().level;
        if (level == null)
            return;
        ifAbsent(level);
        List<UUID> included = new ArrayList<>(clientSignals.get(level).keySet());
        for (Signal signal : signals) {
            UUID uuid = signal.uuid;
            included.remove(uuid);
            signal.level = level;

            if (clientSignals.get(level).containsKey(uuid)) {
                Signal signal1 = clientSignals.get(level).get(uuid);
                signal1.copy(signal);
            } else
                clientSignals.get(level).put(uuid, signal);
        }
        for (UUID uuid : included) {
            clientSignals.get(level).remove(uuid);
        }
    }

    public static void postSignalsToAntenna(Antenna antenna) {
        antenna.signals.clear();

        for (Signal signal : clientSignals.get(antenna.level).values()) {
            AntennaManager.postSignalToAntenna(signal, antenna);
        }
    }

    public static void stopSignal(Signal signal) {
        if (clientSignals.get(signal.level).values().contains(signal)) {
            Signal ourSignal = clientSignals.get(signal.level).get(signal.uuid);
            ourSignal.endTime = ourSignal.lifetime;
            ourSignal.emitting = false;
            clientSignals.get(signal.level).put(signal.uuid, ourSignal);
        }
    }

    private static void ifAbsent(Level level) {
        if (!clientSignals.containsKey(level)) {
            clientSignals.put(level, new HashMap<>());
        }
    }
}
