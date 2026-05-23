package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.ArraySetExecutableAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class SubstringAction extends ArraySetExecutableAction {

    public SubstringAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    protected List<String> arrayKeys() {
        return List.of("string","index");
    }

    @Override
    protected Signal executeArray(Signal[] signals) {
        Signal firstSignal = signals[0];
        Signal secondSignal = signals[1];

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
