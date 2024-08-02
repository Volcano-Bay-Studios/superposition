package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.antenna.AntennaManager;
import org.modogthedev.superposition.system.signal.ClientSignalManager;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class ReceiverBlockEntity extends AntennaActorBlockEntity {

    public ReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.RECEIVER.get(), pos, state);
    }

    int lastSize = 0;
    boolean antennaBrokenLastTick = false;

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        updateAntenna();

        if (antenna != null)
            if (level.isClientSide)
                ClientSignalManager.postSignalsToAntenna(antenna);
            else
                SignalManager.postSignalsToAntenna(antenna);
    }

    @Override
    public List<Signal> getSignals() {
        if (antenna == null) {
            updateAntenna();
        }
        if (antenna == null) {
            return null;
        }
        if (level.isClientSide)
            ClientSignalManager.postSignalsToAntenna(antenna);
        else
            SignalManager.postSignalsToAntenna(antenna);

        return antenna.signals;
    }

    @Override
    public List<Component> getTooltip() {
        AntennaManager.antennaPartUpdate(level,getBlockPos());
        return super.getTooltip();
    }

    @Override
    public void tick() {
        preTick();
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("Receiver Status:"));
        if (antenna != null) {
            if (level.isClientSide) {
                tooltip.add(Component.literal("Antenna Classification - " + classifyAntenna()));
                tooltip.add(Component.literal("Antenna Frequency - " + Mth.frequencyToHzReadable(Mth.antennaSizeToHz(antenna.antennaParts.size()))));
            }
            List<Signal> signals = getSignals();
            int currentSize = signals.size();
            tooltip.add(Component.literal("Signal - "+(signals.isEmpty() ? "NONE":"OK")));
            if (currentSize != lastSize || (antennaBrokenLastTick != (antenna == null))) {
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            }
            if (!signals.isEmpty()) {
                putSignalList(new Object(), signals);
            }
            lastSize = currentSize;
        } else {
            tooltip.add(Component.literal("Antenna Classification - ERROR"));
        }
        antennaBrokenLastTick = antenna == null;
        this.setTooltip(tooltip);
        super.tick();
    }

    @Override
    public void drawExtra() {
        super.drawExtra();
    }
}
