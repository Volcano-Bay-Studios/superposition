package org.modogthedev.superposition.system.signal;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import org.modogthedev.superposition.core.SuperpositionBlocks;
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
        if (level.isClientSide) {
            ClientSignalManager.tick((ClientLevel) level);
            return;
        }
        ifAbsent(level);
        AntennaManager.clearSignals(level);
        List<Signal> signalsForRemoval = new ArrayList<>();
        for (Signal signal : transmittedSignals.get(level)) {
            if (!signal.level.isClientSide) {
                BlockState baseState = level.getBlockState(BlockPos.containing(signal.pos));
                if (!baseState.is(SuperpositionBlocks.TRANSMITTER.get()))
                    stopSignal(signal);
                if (signal.tick()) {
                    signalsForRemoval.add(signal);
                }
            } else {
                signalsForRemoval.add(signal);
            }
        }
        transmittedSignals.get(level).removeAll(signalsForRemoval);

        MinecraftServer minecraftServer = level.getServer();

        for (Player player : level.players()) {
            List<Signal> toSend = new ArrayList<>();
            for (Signal signal : transmittedSignals.get(level)) {
                if (signal.pos.distanceTo(player.position()) < signal.maxDist + 25) {
                    toSend.add(signal);
                }
            }
            CompoundTag wholeTag = new CompoundTag();
            ListTag list = new ListTag();
            for (Signal signal : toSend) {
                CompoundTag tag = new CompoundTag();
                tag.putUUID("uuid", signal.uuid);
                tag.putFloat("x", (float) signal.pos.x);
                tag.putFloat("y", (float) signal.pos.y);
                tag.putFloat("z", (float) signal.pos.z);
                tag.putFloat("amp", signal.amplitude);
                tag.putFloat("freq", signal.frequency);
                tag.putFloat("source_freq",signal.sourceFrequency);
                tag.putFloat("mod", signal.modulation);
                tag.putBoolean("emit", signal.emitting);
                tag.putInt("life", signal.lifetime);
                list.add(tag);
            }
            wholeTag.put("signals", list);
            SignalSyncS2CPacket packet = new SignalSyncS2CPacket(wholeTag);
            Messages.sendToPlayer(packet, (ServerPlayer) player);
        }
    }

    public static void postSignalsToAntenna(Antenna antenna) {
        antenna.signals.clear();
        for (Signal signal : transmittedSignals.get(antenna.level)) {
            AntennaManager.postSignalToAntenna(signal, antenna);
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
            transmittedSignals.get(signal.level).set(transmittedSignals.get(signal.level).indexOf(signal), signal);
        } else {
            transmittedSignals.get(signal.level).add(signal);
        }
    }

    public static void stopSignal(Signal signal) {
        if (signal.level.isClientSide) {
            ClientSignalManager.stopSignal(signal);
        } else if (transmittedSignals.get(signal.level).contains(signal)) {
            Signal ourSignal = transmittedSignals.get(signal.level).get(transmittedSignals.get(signal.level).indexOf(signal));
            ourSignal.endTime = ourSignal.lifetime;
            ourSignal.emitting = false;
            transmittedSignals.get(signal.level).set(transmittedSignals.get(signal.level).indexOf(signal), ourSignal);
        }
    }

    public static Signal randomSignal(List<Signal> signalList) {
        if (signalList == null || signalList.isEmpty())
            return null;
        int ordinal = (int) Math.floor(Math.random() * signalList.size());
        return signalList.get(ordinal);
    }
}
