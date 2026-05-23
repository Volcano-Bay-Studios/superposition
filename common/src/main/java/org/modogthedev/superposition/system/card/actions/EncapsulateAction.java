package org.modogthedev.superposition.system.card.actions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.ArraySetExecutableAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class EncapsulateAction extends ArraySetExecutableAction {

    public EncapsulateAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    protected List<String> arrayKeys() {
        return List.of("data", "key");
    }

    @Override
    protected Signal executeArray(Signal[] signals) {
        Signal firstSignal = signals[0];
        Signal secondSignal = signals[1];

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
