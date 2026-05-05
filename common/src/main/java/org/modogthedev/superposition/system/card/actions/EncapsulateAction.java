package org.modogthedev.superposition.system.card.actions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.BiModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class EncapsulateAction extends Action implements BiModifyAction {

    public EncapsulateAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal firstSignal, Signal secondSignal) {
        if (firstSignal.getEncodedData() != null) {
            CompoundTag tag = new CompoundTag();
            String key = "0";
            if (secondSignal != null && secondSignal.getEncodedData() != null) {
                key = secondSignal.getEncodedData().stringValue();
            }
            tag.putString(key, firstSignal.getEncodedData().stringValue());
            firstSignal.encode(tag);
        } else {
            firstSignal.encode(new CompoundTag());
        }
        return firstSignal;
    }
}
