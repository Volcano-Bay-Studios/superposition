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
    public Signal modify(Signal signal, Signal periphrealSignal) {
        if (periphrealSignal != null && signal.getEncodedData() != null && periphrealSignal.getEncodedData() != null) {
            signal.setEncodedData(signal.getEncodedData().getTagKey(periphrealSignal.getEncodedData().stringValue()));
        }
        return signal;
    }
}
