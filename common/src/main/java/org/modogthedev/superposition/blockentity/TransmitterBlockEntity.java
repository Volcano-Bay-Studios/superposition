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
            frequency = antenna.getFrequency();
        }
        if (antenna != null && level.isClientSide) {
            tooltip.add(Component.literal("Antenna Classification - " + this.classifyAntenna()));
            tooltip.add(Component.literal("Antenna Frequency - " + SuperpositionMth.formatHz(frequency)));
        }
        boolean noSignal = false;
        if (antenna != null) {
            Signal signalForBroadcast = getSignal();
            if (signalForBroadcast != null) {
                if (level.hasNeighborSignal(this.getBlockPos())) { //TODO: borken
                    signalForBroadcast.getPos().set(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
                    signalForBroadcast.setEmitting(true);
                    signalForBroadcast.level = level;
                    signalForBroadcast.setSourceAntenna(this.getBlockPos());
                    signalForBroadcast.mulAmplitude(1 / Math.max(1, Math.abs((signalForBroadcast.getFrequency() - frequency) / 100000)));
                    if (signalForBroadcast.getAmplitude() > 0.05f) {
                        SignalManager.updateSignal(signalForBroadcast);
                        signal = signalForBroadcast;
                        if (level.isClientSide) {
                            tooltip.add(Component.literal("Broadcast Frequency - " + SuperpositionMth.formatHz(signalForBroadcast.getFrequency())));
                        }
                    } else {
                        this.stopTransmission();
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
