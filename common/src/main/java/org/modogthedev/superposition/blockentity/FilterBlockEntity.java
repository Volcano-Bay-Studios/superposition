package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class FilterBlockEntity  extends SignalActorBlockEntity implements TickableBlockEntity {

    private float minFilter = 0;
    private float maxFilter = 64;

    public FilterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(SuperpositionBlockEntities.FILTER.get(), pPos, pBlockState);
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
        preTick();
        List<Component> tooltip = new ArrayList<>();
        this.setTooltip(tooltip);
        super.tick();
    }

    @Override
    public void putSignalList(Object nextCall, List<Signal> list) {
        super.putSignalList(nextCall, modulateSignals(list,true));
    }
}
