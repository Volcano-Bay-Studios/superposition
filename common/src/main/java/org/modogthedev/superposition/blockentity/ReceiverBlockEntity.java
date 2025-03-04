package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.antenna.AntennaManager;
import org.modogthedev.superposition.system.signal.ClientSignalManager;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.ArrayList;
import java.util.List;

public class ReceiverBlockEntity extends AntennaActorBlockEntity {
    Object ourCall;

    public ReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.RECEIVER.get(), pos, state);
    }

    int lastSize = 0;
    boolean antennaBrokenLastTick = false;

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

    @Override
    public List<Signal> getSignals() {
        if (antenna == null) {
            return null;
        }
        antenna.signals.clear();
        if (level.isClientSide)
            ClientSignalManager.postSignalsToAntenna(antenna);
        else
            SignalManager.postSignalsToAntenna(antenna);

        return antenna.signals;
    }

    @Override
    public List<Component> getTooltip() {
        AntennaManager.antennaPartUpdate(level, getBlockPos());
        return super.getTooltip();
    }

    @Override
    public void tick() {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("Receiver Status:"));
        if (antenna != null) {
            if (level.isClientSide) {
                tooltip.add(Component.literal("Antenna Classification - " + classifyAntenna()));
                tooltip.add(Component.literal("Antenna Frequency - " + SuperpositionMth.frequencyToHzReadable(SuperpositionMth.antennaSizeToHz(antenna.antennaParts.size()))));
                float bonusFrequency = getBounusFrequency();
                if (bonusFrequency != 0) {
                    tooltip.add(Component.literal("Actual Frequency - " + SuperpositionMth.frequencyToHzReadable(SuperpositionMth.antennaSizeToHz(antenna.antennaParts.size()) + bonusFrequency)));
                }
            }
            List<Signal> signals = getSignals();
            updatePutSignals(signals);
            int currentSize = signals.size();
            tooltip.add(Component.literal("Signal - " + (signals.isEmpty() ? "NONE" : "OK")));
            if (currentSize != lastSize || (antennaBrokenLastTick != (antenna == null))) {
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            }
            if (!signals.isEmpty()) {
                ourCall = new Object();
                putSignalList(ourCall, signals);
            }
            lastSize = currentSize;
        } else {
            tooltip.add(Component.literal("Antenna Classification - ERROR"));
        }
        antennaBrokenLastTick = (antenna == null);
        this.setTooltip(tooltip);
        super.tick();
    }

    @Override
    public void putSignalList(Object nextCall, List<Signal> list) {
        if (nextCall == ourCall)
            super.putSignalList(nextCall, list);
    }

    @Override
    public void drawExtra() {
        super.drawExtra();
    }
}
