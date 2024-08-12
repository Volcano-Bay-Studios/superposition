package org.modogthedev.superposition.system.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.modogthedev.superposition.screens.WidgetScreen;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.Mth;

import java.awt.*;
import java.util.List;

public class BandPassFilter extends Filter {
    float lowFrequency;
    float highFrequency;

    @Override
    public boolean passSignal(Signal signal) {
        return (signal.frequency) > (lowFrequency * 100000) && signal.frequency < (Math.abs(158 - highFrequency) * 100000);

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
        return "Band Pass Filter - " + Mth.frequencyToHzReadable(lowFrequency) + "-" + Mth.frequencyToHzReadable(highFrequency);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(SuperpositionItems.BAND_PASS_FILTER.get());
    }

    @Override
    public Color getColor() {
        return new Color(127, 246, 95);
    }
}