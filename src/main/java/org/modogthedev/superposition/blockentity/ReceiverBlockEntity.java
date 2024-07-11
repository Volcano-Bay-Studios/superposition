package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.system.signal.ClientSignalManager;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.AntennaActorBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class ReceiverBlockEntity extends AntennaActorBlockEntity {
    public ReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntity.RECEIVER.get(), pos, state);
    }

    int lastSize = 0;

    @Override
    public void onLoad() {
        super.onLoad();
        updateAntenna();
        if (antenna != null)
            if (level.isClientSide)
                ClientSignalManager.postSignalsToAntenna(antenna);
            else
                SignalManager.postSignalsToAntenna(antenna);
    }

    public List<Signal> getSignals() {
        if (antenna == null) {
            updateAntenna();
        }
        if (antenna == null) {
            return null;
        }
//        if (level.isClientSide)
////            ClientSignalManager.postSignalsToAntenna(antenna);
//        else
//            SignalManager.postSignalsToAntenna(antenna);

        return antenna.signals;
    }

    @Override
    public void tick() {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("Receiver Status:"));
        if (antenna != null) {
            tooltip.add(Component.literal("Antenna - "+(antenna.antennaParts.size() > 2 ? "TOO SMALL":"OK")));
            List<Signal> signals = getSignals();
            int currentSize = signals.size();
            tooltip.add(Component.literal("Signal - "+(signals.isEmpty() ? "NONE":"OK")));
            if (currentSize != lastSize) {
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            }
            if (!signals.isEmpty()) {
                putSignalList(new Object(), signals);

            }
            lastSize = currentSize;
        } else {
            tooltip.add(Component.literal("Antenna - ERROR"));
        }
        this.setTooltip(tooltip);
        super.tick();
    }

    @Override
    public void drawExtra() {
        super.drawExtra();
    }
}
