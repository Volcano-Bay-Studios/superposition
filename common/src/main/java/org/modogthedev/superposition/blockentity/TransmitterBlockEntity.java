package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.ArrayList;
import java.util.List;

public class TransmitterBlockEntity extends AntennaActorBlockEntity {

    private Signal signal;

    public TransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.TRANSMITTER.get(), pos, state);
    }

    @Override
    public void tick() {
        List<Component> tooltip = new ArrayList<>();
//        System.out.println(SignalManager.transmittedSignals.get(level).size());
        BlockPos sidedPos = this.getSwappedPos();
        tooltip.add(Component.literal("Transmitter Status:"));
        float frequency = 0;
        if (antenna != null) {
            frequency = SuperpositionMth.antennaSizeToHz(antenna.antennaParts.size());
        }
        if (antenna != null && level.isClientSide) {
            tooltip.add(Component.literal("Antenna Classification - " + this.classifyAntenna()));
            tooltip.add(Component.literal("Antenna Frequency - " + SuperpositionMth.frequencyToHzReadable(frequency)));
        }
        boolean noSignal = false;
        if (antenna != null) {
            Signal signalForBroadcast = getSignal();
            if (signalForBroadcast != null) {
                if (level.hasSignal(this.getBlockPos(), this.getSwappedSide())) { //TODO: borken
                    signalForBroadcast.getPos().set(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
                    signalForBroadcast.setEmitting(true);
                    signalForBroadcast.level = level;
                    signalForBroadcast.setSourceAntenna(this.getBlockPos(), antenna.antennaParts.size());
                    signalForBroadcast.mulAmplitude(1/Math.max(1,Math.abs((signalForBroadcast.getFrequency()-frequency)/100000)));
                    SignalManager.updateSignal(signalForBroadcast);
                    signal = signalForBroadcast;
                    if (level.isClientSide) {
                        tooltip.add(Component.literal("Broadcast Frequency - " + SuperpositionMth.frequencyToHzReadable(signalForBroadcast.getFrequency())));
                    }
                } else if (signal != null) {
                    this.stopTransmission();
                }
            } else {
                if (signal != null) {
                    this.stopTransmission();
                }
                noSignal = true;
            }
        } else if (signal != null) {
            this.stopTransmission();
        }
        tooltip.add(Component.literal("Signal - " + ((signal != null) ? "BROADCASTING" : (noSignal ? "NO SIGNAL" : "OFFLINE"))));
        this.setTooltip(tooltip);
        super.tick();
    }

    public void stopTransmission() {
        SignalManager.stopSignal(signal);
        signal = null;
    }
}
