package org.modogthedev.superposition.system.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.modogthedev.superposition.screens.WidgetScreen;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.awt.*;
import java.util.List;

public class BandPassFilter extends Filter {
    float lowFrequency;
    float highFrequency;

    public BandPassFilter(ResourceLocation filter) {
        super(filter);
    }

    public BandPassFilter() {
        super();
    }

    @Override
    public boolean passSignal(Signal signal) {
        return (signal.getFrequency()) > (lowFrequency * 100000) && signal.getFrequency() < (Math.abs(158 - highFrequency) * 100000);
    }

    @Override
    public void save(CompoundTag tag) {
        tag.putFloat("lowFrequency", lowFrequency);
        tag.putFloat("highFrequency", highFrequency);
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag != null) {
            lowFrequency = tag.getFloat("lowFrequency");
            highFrequency = tag.getFloat("highFrequency");
        }
    }

    @Override
    public void updateFromDials(List<WidgetScreen.Dial> dials) {
        lowFrequency = dials.get(0).scrolledAmount;
        highFrequency = dials.get(1).scrolledAmount;
    }

    @Override
    public void updateDials(List<WidgetScreen.Dial> dials) {
        dials.get(0).scrolledAmount = lowFrequency;
        dials.get(1).scrolledAmount = highFrequency;
    }

    @Override
    public String toString() {
        return "Band Pass Filter - " + SuperpositionMth.frequencyToHzReadable(lowFrequency) + " - " + SuperpositionMth.frequencyToHzReadable(Math.abs(158 - highFrequency));
    }

    @Override
    public String getTooltip() {
        return SuperpositionMth.frequencyToHzReadable(lowFrequency) + " - " + SuperpositionMth.frequencyToHzReadable(Math.abs(158 - highFrequency));
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(SuperpositionItems.BAND_PASS_FILTER.get());
    }

    @Override
    public Color getColor() {
        return new Color(127, 246, 95);
    }

    @Override
    public Filter create() {
        return new BandPassFilter(getSelfReference());
    }
}