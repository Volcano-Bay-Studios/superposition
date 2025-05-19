package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.BiModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class RetriveCard extends Action implements BiModifyAction {

    public RetriveCard(ResourceLocation card) {
        super(card);
    }

    @Override
    public Signal modify(Signal signal, Signal periphrealSignal) {
        if (periphrealSignal != null && signal.getEncodedData() != null && periphrealSignal.getEncodedData() != null) {
            signal.setEncodedData(signal.getEncodedData().getTagKey(periphrealSignal.getEncodedData().stringValue()));
        }
        return signal;
    }
}
