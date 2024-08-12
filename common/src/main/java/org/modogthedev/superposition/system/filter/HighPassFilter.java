package org.modogthedev.superposition.system.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.modogthedev.superposition.screens.WidgetScreen;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.Mth;

import java.awt.*;
import java.util.List;

public class HighPassFilter extends Filter {
    float frequency;

    @Override
    public boolean passSignal(Signal signal) {
        return signal.frequency > frequency;
    }

    @Override
    public void save(CompoundTag tag) {
        tag.putFloat("frequency", frequency);
    }

    @Override
    public void load(CompoundTag tag) {
        frequency = tag.getFloat("frequency");
    }

    @Override
    public String toString() {
        return "High Pass Filter - " + Mth.frequencyToHzReadable(frequency);
    }
    @Override
    public void updateFromDials(List<WidgetScreen.Dial> dialList) {
        frequency = dialList.get(0).scrolledAmount;
    }

    @Override
    public void updateDials(List<WidgetScreen.Dial> dials) {
        dials.get(0).scrolledAmount = frequency;
    }
    @Override
    public Color getColor() {
        return new Color(246, 95, 95);
    }
    @Override
    public ItemStack getItem() {
        return new ItemStack(SuperpositionItems.HIGH_PASS_FILTER.get());
    }
}