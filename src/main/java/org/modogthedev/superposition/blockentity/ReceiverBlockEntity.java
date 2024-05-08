package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.ModBlockEntity;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaManager;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.AntennaActorBlockEntity;

import java.util.List;

public class ReceiverBlockEntity extends AntennaActorBlockEntity {
    public ReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.RECEIVER.get(), pos, state);
    }

    int lastSize = 0;

    @Override
    public void onLoad() {
        super.onLoad();
        updateAntenna();
    }

    public List<Signal> getSignals() {
        if (antenna == null)
            updateAntenna();
        if (antenna == null) {
            System.out.println("womp womp");
            return null;
        }
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
        }
    }

}
