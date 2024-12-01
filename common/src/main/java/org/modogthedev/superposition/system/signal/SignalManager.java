package org.modogthedev.superposition.system.signal;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.core.SuperpositionBlocks;
import org.modogthedev.superposition.networking.packet.SignalSyncS2CPacket;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SignalManager {
    public static HashMap<Level, List<Signal>> transmittedSignals = new HashMap<>();

    public static void tick(ServerLevel level) {
        ifAbsent(level);
        AntennaManager.clearSignals(level);

        Iterator<Signal> iterator = transmittedSignals.get(level).iterator();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        while (iterator.hasNext()) {
            Signal signal = iterator.next();
            Vector3d pos = signal.getPos();
            blockPos.set(pos.x,pos.y,pos.z);
            BlockState baseState = level.getBlockState(blockPos);
            if (!baseState.is(SuperpositionBlocks.TRANSMITTER.get())) {
                stopSignal(signal);
            }
            if (signal.tick()) {
                iterator.remove();
            }
        }

        for (Player player : level.players()) {
            if (!(player instanceof ServerPlayer serverPlayer)) {
                continue;
            }

            List<Signal> toSend = new ArrayList<>();
            for (Signal signal : transmittedSignals.get(level)) {
                Vec3 pos = player.position();
                if (signal.getPos().distanceSquared(pos.x, pos.y, pos.z) < (signal.getMaxDist() + 25) * (signal.getMaxDist() + 25)) {
                    toSend.add(signal);
                }
            }

            if (!toSend.isEmpty()) {
                VeilPacketManager.player(serverPlayer).sendPacket(new SignalSyncS2CPacket(toSend));
            }
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
        if (signal.level.isClientSide) {
            return;
        }
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
            ourSignal.stop();
            transmittedSignals.get(signal.level).set(transmittedSignals.get(signal.level).indexOf(signal), ourSignal);
        }
    }

    public static Signal randomSignal(List<Signal> signalList) {
        if (signalList == null || signalList.isEmpty()) {
            return null;
        }
        int ordinal = (int) Math.floor(Math.random() * signalList.size());
        return signalList.get(ordinal);
    }
}
