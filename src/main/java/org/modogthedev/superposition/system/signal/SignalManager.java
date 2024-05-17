package org.modogthedev.superposition.system.signal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import org.modogthedev.superposition.networking.Messages;
import org.modogthedev.superposition.networking.packet.SignalSyncS2CPacket;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignalManager {
    public static HashMap<Level, List<Signal>> transmittedSignals = new HashMap<>();

    public static void tick(TickEvent.LevelTickEvent event) {
        Level level = event.level;
        ifAbsent(level);
        AntennaManager.clearSignals(level);
        List<Signal> signalsForRemoval = new ArrayList<>();
        for (Signal signal : transmittedSignals.get(level)) {
            AntennaManager.postSignal(signal);
            if (signal.tick()) {
                signalsForRemoval.add(signal);
            }
        }
        transmittedSignals.get(level).removeAll(signalsForRemoval);

        if (!level.isClientSide) {
            MinecraftServer minecraftServer = level.getServer();

            for (Player player : level.players()) {
                List<Signal> toSend = new ArrayList<>();
                for (Signal signal : transmittedSignals.get(level)) {
                    if (signal.pos.distanceTo(player.position()) < signal.maxDist+25) {
                        toSend.add(signal);
                    }
                }
                CompoundTag wholeTag = new CompoundTag();
                ListTag list = new ListTag();
                for (Signal signal : toSend) {
                    CompoundTag tag = new CompoundTag();
                    tag.putUUID("uuid",signal.uuid);
                    tag.putFloat("x", (float) signal.pos.x);
                    tag.putFloat("y", (float) signal.pos.y);
                    tag.putFloat("z", (float) signal.pos.z);
                    tag.putFloat("amp", signal.amplitude);
                    tag.putFloat("freq", signal.frequency);
                    tag.putFloat("mod", signal.modulation);
                    tag.putBoolean("emit",signal.emitting);
                    tag.putInt("life", signal.lifetime);
                    list.add(tag);
                }
                wholeTag.put("signals",list);
                SignalSyncS2CPacket packet = new SignalSyncS2CPacket(wholeTag);
                Messages.sendToPlayer(packet, (ServerPlayer) player);
            }
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
