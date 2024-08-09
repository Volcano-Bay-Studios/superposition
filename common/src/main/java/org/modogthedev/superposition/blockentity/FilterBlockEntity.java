package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.item.FilterItem;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class FilterBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {

    private float minFilter = 0;
    private float maxFilter = 64;
    public List<Signal> unmodulated;
    private FilterItem.FilterType type = FilterItem.FilterType.NONE;

    public FilterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(SuperpositionBlockEntities.FILTER.get(), pPos, pBlockState);
    }

    public float[] readFilterData() {
        switch (type){
            case HIGH_PASS -> {
                return new float[]{maxFilter, minFilter};
            }
            default -> {
                return new float[]{minFilter, maxFilter};
            }
        }
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        if (!passValue(signal.frequency)) {
            return null;
        }
        return super.modulateSignal(signal, updateTooltip);
    }

    public FilterItem.FilterType getFilterType() {
        return type;
    }

    @Override
    public List<Signal> modulateSignals(List<Signal> signalList, boolean updateTooltip) {
        if (level.isClientSide && updateTooltip)
            unmodulated = signalList;
        List<Signal> finalSignals = new ArrayList<>();
        for (Signal signal : signalList) {
            if (passValue(signal.frequency)) {
                finalSignals.add(signal);
            }
        }
        return finalSignals;
    }
    public boolean passValue(float value) {
        return (value) > (minFilter * 100000) && value < (Math.abs(158-maxFilter) * 100000);
    }
    public boolean passCustomValue(float value,float min, float max) {
        return (value) > (min) && value < max;
    }

    @Override
    public void tick() {
        preTick();
        List<Component> tooltip = new ArrayList<>();
        this.setTooltip(tooltip);
        super.tick();
    }

    public void setFilter(float value1, float value2, FilterItem.FilterType type) {
        switch (type) {
            case LOW_PASS -> minFilter = value1;
            case HIGH_PASS -> maxFilter = value1;
            case BAND_PASS -> {
                minFilter = value1;
                maxFilter = value2;
            }
            default -> {
                minFilter = value1;
                maxFilter = value2;
            }
        }
        this.type = type;
    }

    @Override
    public void putSignalList(Object nextCall, List<Signal> list) {
        super.putSignalList(nextCall, modulateSignals(list, false));
    }
    public List<Signal> getUnmodulated() {
        return unmodulated;
    }
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putFloat("minFilter", minFilter);
        pTag.putFloat("maxFilter", maxFilter);
        pTag.putInt("type", type.ordinal());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        minFilter = pTag.getFloat("minFilter");
        maxFilter = pTag.getFloat("maxFilter");
        type = FilterItem.FilterType.values()[pTag.getInt("type")];
    }
}
