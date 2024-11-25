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

    public List<Signal> unmodulated;
    private Filter type = null;

    public FilterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(SuperpositionBlockEntities.FILTER.get(), pPos, pBlockState);
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        if (!passSignal(signal)) {
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
            if (passSignal(signal)) {
                finalSignals.add(signal);
            }
        }
        return finalSignals;
    }

    public boolean passSignal(Signal signal) {
        if (type != null)
            return type.passSignal(signal);
        return true;
    }

    @Override
    public void tick() {
        preTick();
        resetTooltip();
        if (level.isClientSide && getFilterType() != null) {
            addTooltip(Component.literal("Filter Status: "));
            addTooltip(Component.translatable(getFilterType().toString()));
        }
        super.tick();
    }

    public void setFilter(Filter filter) {
        this.type = filter;
        sendData();
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
            pTag.putString("namespace", type.getSelfReference().getNamespace());
            pTag.putString("path", type.getSelfReference().getPath());
            type.save(pTag);
        }
    }


    @Override
    public void loadSyncedData(CompoundTag tag) {
        if (tag.contains("swap"))
            super.loadSyncedData(tag);
        if (type != null && tag.contains("path"))
            type.load(tag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        type = SuperpositionFilters.FILTERS.getRegistrar().get(new ResourceLocation(pTag.getString("namespace"), pTag.getString("path")));
        if (type != null) {
            type = type.create();
            type.load(pTag);
        }
    }
}
