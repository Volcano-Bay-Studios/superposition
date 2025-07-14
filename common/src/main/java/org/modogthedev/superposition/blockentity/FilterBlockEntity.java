package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionFilters;
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
        return signal;
    }

    public Filter getFilter() {
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
        resetTooltip();
        if (level.isClientSide && getFilter() != null) {
            addTooltip(Component.literal("Filter Status: "));
            addTooltip(Component.translatable(getFilter().toString()));
        }
        super.tick();
    }

    public void setFilter(Filter filter) {
        this.type = filter;
        sendData();
    }

    public List<Signal> getUnmodulated() {
        return unmodulated;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (type != null) {
            tag.putString("namespace", type.getSelfReference().getNamespace());
            tag.putString("path", type.getSelfReference().getPath());
            type.save(tag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        type = SuperpositionFilters.FILTERS.asVanillaRegistry().get(ResourceLocation.fromNamespaceAndPath(tag.getString("namespace"), tag.getString("path")));
        if (type != null) {
            type = type.create();
            type.load(tag);
        }
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        if (tag.contains("swap"))
            super.loadSyncedData(tag);
        if (type != null && tag.contains("path"))
            type.load(tag);
    }
}
