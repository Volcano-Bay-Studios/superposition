package org.modogthedev.superposition.system.signal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ClientSignalManager {
    public static HashMap<Level, HashMap<UUID, Signal>> clientSignals = new HashMap<>();

    public static void tick(ClientLevel level) {
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

    public static void processTag(CompoundTag wholeTag) {
        Level level = Minecraft.getInstance().level;
        if (level == null)
            return;
        List<UUID> included = new ArrayList<>(clientSignals.get(level).keySet());
        ListTag list = wholeTag.getList("signals", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag tag = (CompoundTag) t;
            UUID uuid = tag.getUUID("uuid");
            included.remove(uuid);
            Vec3 pos = new Vec3(tag.getFloat("x"), tag.getFloat("y"), tag.getFloat("z"));
            Signal signal = new Signal(pos, level, tag.getFloat("freq"), tag.getFloat("amp"),tag.getFloat("source_freq"));
            signal.modulation = tag.getFloat("mod");
            signal.emitting = tag.getBoolean("emit");
            signal.lifetime = tag.getInt("life");
            signal.level = level;

            if (clientSignals.get(level).containsKey(uuid)) {
                Signal signal1 = clientSignals.get(level).get(uuid);
                signal1.modulation = signal.modulation;
                signal1.emitting = signal.emitting;
                signal1.lifetime = signal.lifetime;
                signal1.level = signal.level;
                signal1.frequency = signal.frequency;
                signal1.amplitude = signal.amplitude;
                signal1.pos = signal.pos;
                signal1.sourceFrequency = signal.sourceFrequency;
            } else
                clientSignals.get(level).put(uuid, signal);
        }
        for (UUID uuid: included) {
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
