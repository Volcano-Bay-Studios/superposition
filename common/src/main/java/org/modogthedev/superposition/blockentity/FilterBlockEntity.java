package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionFilters;
import org.modogthedev.superposition.item.FilterItem;
import org.modogthedev.superposition.system.filter.Filter;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class FilterBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {

    private float minFilter = 0;
    private float maxFilter = 64;
    public List<Signal> unmodulated;
    private Filter type = null;

    public FilterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(SuperpositionBlockEntities.FILTER.get(), pPos, pBlockState);
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        if (!passValue(signal.frequency)) {
            return null;
        }
        return super.modulateSignal(signal, updateTooltip);
    }

    public Filter getFilterType() {
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
        return (value) > (minFilter * 100000) && value < (Math.abs(158 - maxFilter) * 100000);
    }

    public boolean passCustomValue(float value, float min, float max) {
        return (value) > (min) && value < max;
    }

    @Override
    public void tick() {
        preTick();
        List<Component> tooltip = new ArrayList<>();
        this.setTooltip(tooltip);
        super.tick();
    }

    public void setFilter(Filter filter) {
        this.type = filter;
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
        if (type != null) {
            type.save(pTag);
            pTag.putString("namespace", SuperpositionFilters.FILTERS.getRegistrar().getId(type).getNamespace());
            pTag.putString("path", SuperpositionFilters.FILTERS.getRegistrar().getId(type).getPath());
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        type = SuperpositionFilters.FILTERS.getRegistrar().get(new ResourceLocation(pTag.getString("namespace"), pTag.getString("path")));
        if (type != null)
            type.load(pTag);
    }
}
