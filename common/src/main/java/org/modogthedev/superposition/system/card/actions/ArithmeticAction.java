package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.BiModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class ArithmeticAction extends Action implements BiModifyAction {

    public ArithmeticAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal signal, Signal periphrealSignal) {
        if (signal != null && periphrealSignal != null && periphrealSignal.getEncodedData() != null) {
            signal.setEncodedData(periphrealSignal.getEncodedData());
        }
        return signal;
    }
}
