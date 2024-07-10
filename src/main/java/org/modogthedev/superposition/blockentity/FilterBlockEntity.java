package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlocks;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorBlockEntity;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class FilterBlockEntity  extends SignalActorBlockEntity implements TickableBlockEntity {
    float minFilter = 0;
    float maxFilter = 64;
    public FilterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(SuperpositionBlockEntity.FILTER.get(), pPos, pBlockState);
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        if (signal.frequency < minFilter || signal.frequency > maxFilter) {
            return null;
        }
        return super.modulateSignal(signal,updateTooltip);
    }

    @Override
    public List<Signal> modulateSignals(List<Signal> signalList, boolean updateTooltip) {
        List<Signal> finalSignals = new ArrayList<>();
        for (Signal signal: signalList) {
            if (signal.frequency > minFilter && signal.frequency < maxFilter) {
                finalSignals.add(signal);
            }
        }
        return finalSignals;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void putSignalList(Object nextCall, List<Signal> list) {
        super.putSignalList(nextCall, modulateSignals(list,true));
    }
}
