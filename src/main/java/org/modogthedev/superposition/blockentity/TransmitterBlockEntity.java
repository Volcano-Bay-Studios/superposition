package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.AntennaActorBlockEntity;
import org.modogthedev.superposition.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class TransmitterBlockEntity extends AntennaActorBlockEntity {
    Signal signal;
    public TransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntity.TRANSMITTER.get(), pos, state);
    }


    @Override
    public void tick() {
        preTick();
        List<Component> tooltip = new ArrayList<>();
//        System.out.println(SignalManager.transmittedSignals.get(level).size());
        BlockPos sidedPos = getSwappedPos();
        tooltip.add(Component.literal("Transmitter Status:"));
        float frequency = 0;
        if (antenna!= null) {
            frequency =  Mth.antennaSizeToHz(antenna.antennaParts.size());
        }
        if (antenna != null && level.isClientSide) {
            tooltip.add(Component.literal("Antenna Classification - " + classifyAntenna()));
            tooltip.add(Component.literal("Antenna Frequency - " + Mth.frequencyToHzReadable(frequency)));
        }
        boolean noSignal = false;
        if (antenna != null) {
            Signal signalForBroadcast = createSignal(new Object());
            if (signalForBroadcast != null) {
                if (level.getSignal(getBlockPos(),getSwappedSide()) > 0) { //TODO borken
                    signalForBroadcast.pos = new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
                    signalForBroadcast.emitting = true;
                    signalForBroadcast.level = level;
                    frequency += signalForBroadcast.frequency;
                    signalForBroadcast.frequency = frequency;
                    SignalManager.addSignal(signalForBroadcast);
                    signal = signalForBroadcast;
                    if (level.isClientSide)
                        tooltip.add(Component.literal("Broadcast Frequency - " + Mth.frequencyToHzReadable(signalForBroadcast.frequency)));
                } else if (signal != null)
                    stopTransmission();
            } else {
                if (signal != null)
                    stopTransmission();
                noSignal = true;
            }
        } else if (signal != null) {
            stopTransmission();
        }
        tooltip.add(Component.literal("Signal - "+((signal != null) ? "BROADCASTING" : (noSignal?"NO SIGNAL":"OFFLINE"))));
        this.setTooltip(tooltip);
        super.tick();
    }
    public void stopTransmission() {
        endSignal(new Object());
        SignalManager.stopSignal(signal);
        signal = null;
    }
}
