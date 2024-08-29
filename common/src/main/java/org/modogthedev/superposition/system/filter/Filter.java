package org.modogthedev.superposition.system.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.modogthedev.superposition.screens.WidgetScreen;
import org.modogthedev.superposition.system.signal.Signal;

import java.awt.*;
import java.util.List;

public abstract class Filter {
    private ResourceLocation selfReference;
    public Filter(ResourceLocation filter) {
        this.selfReference = filter;
    }
    public Filter() {
    }
    public abstract boolean passSignal(Signal signal);

    public abstract void save(CompoundTag tag);

    public abstract void load(CompoundTag tag);

    public abstract ItemStack getItem();

    public abstract void updateFromDials(List<WidgetScreen.Dial> dials);

    public abstract void updateDials(List<WidgetScreen.Dial> dials);

    public abstract Color getColor();
    public abstract Filter create();

    public ResourceLocation getSelfReference() {
        return selfReference;
    }

    public boolean openCustomScreen() {
        return false;
    }
}