package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.BiModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class RetriveAction extends Action implements BiModifyAction {

    public RetriveAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal firstSignal, Signal secondSignal) {
        if (secondSignal != null && firstSignal.getEncodedData() != null && secondSignal.getEncodedData() != null) {
            firstSignal.setEncodedData(firstSignal.getEncodedData().getTagKey(secondSignal.getEncodedData().stringValue()));
        }
        return firstSignal;
    }
}
