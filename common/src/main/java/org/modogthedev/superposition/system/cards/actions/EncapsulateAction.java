package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.BiModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class EncapsulateAction extends Action implements BiModifyAction {

    public EncapsulateAction(ResourceLocation card) {
        super(card);
    }

    @Override
    public Signal modify(Signal signal, Signal periphrealSignal) {
        if (signal.getEncodedData() != null) {
            CompoundTag tag = new CompoundTag();
            String key = "0";
            if (periphrealSignal != null && periphrealSignal.getEncodedData() != null) {
                key = periphrealSignal.getEncodedData().stringValue();
            }
            tag.putString(key, signal.getEncodedData().stringValue());
            signal.encode(tag);
        } else {
            signal.encode(new CompoundTag());
        }
        return signal;
    }
}
