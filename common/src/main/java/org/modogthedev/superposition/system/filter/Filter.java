package org.modogthedev.superposition.system.filter;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.core.SuperpositionFilters;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.modogthedev.superposition.core.SuperpositionRegistries;
import org.modogthedev.superposition.screens.WidgetScreen;
import org.modogthedev.superposition.system.signal.Signal;

import java.awt.*;
import java.util.List;

public abstract class Filter {
    public abstract boolean passSignal(Signal signal);

    public abstract void save(CompoundTag tag);

    public abstract void load(CompoundTag tag);

    public abstract ItemStack getItem();

    public abstract void updateFromDials(List<WidgetScreen.Dial> dials);

    public abstract void updateDials(List<WidgetScreen.Dial> dials);

    public abstract Color getColor();

    public boolean openCustomScreen() {
        return false;
    }
}