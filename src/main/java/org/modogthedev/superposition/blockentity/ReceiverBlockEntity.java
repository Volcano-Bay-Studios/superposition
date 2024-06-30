package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.AntennaActorBlockEntity;

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
            SignalManager.postSignalsToAntenna(antenna);
    }

    public List<Signal> getSignals() {
        if (antenna == null) {
            updateAntenna();
        }
        if (antenna == null) {
            return null;
        }
        SignalManager.postSignalsToAntenna(antenna);
        return antenna.signals;
    }

    @Override
    public void tick() {
        super.tick();

        if (antenna != null) {
            List<Signal> signals = getSignals();
            int currentSize = signals.size();
            if (currentSize != lastSize) {
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
                lastSize = currentSize;
            }
            if (!signals.isEmpty()) {
                putSignalList(new Object(), signals);
            }
        }
    }

}
