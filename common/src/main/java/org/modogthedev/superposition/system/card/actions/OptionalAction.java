package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.BiModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class OptionalAction extends Action implements BiModifyAction {

    public OptionalAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal firstSignal, Signal secondSignal) {
        if (firstSignal != null && secondSignal != null && secondSignal.getEncodedData() != null) {
            if (secondSignal.getEncodedData().booleanValue()) {
                return firstSignal;
            }
        }
        return null;
    }
}
