package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.BiModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class SubstringAction extends Action implements BiModifyAction {

    public SubstringAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal firstSignal, Signal secondSignal) {
        if (secondSignal != null && firstSignal.getEncodedData() != null && secondSignal.getEncodedData() != null) {
            String s = firstSignal.getEncodedData().stringValue();
            int cutPosition = secondSignal.getEncodedData().intValue();
            if (s != null) {
                if (cutPosition >= 0) {
                    firstSignal.encode(s.substring(0, Math.max(0, s.length() - cutPosition)));
                } else {
                    firstSignal.encode(s.substring(-cutPosition));
                }
            }
        }
        return firstSignal;
    }
}
