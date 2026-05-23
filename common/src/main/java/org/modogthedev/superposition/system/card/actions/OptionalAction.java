package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.card.ArraySetExecutableAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class OptionalAction extends ArraySetExecutableAction {

    public OptionalAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    protected List<String> arrayKeys() {
        return List.of("data","boolean");
    }

    @Override
    protected Signal executeArray(Signal[] signals) {
        Signal firstSignal = signals[0];
        Signal secondSignal = signals[1];

        if (firstSignal != null && secondSignal != null && secondSignal.getEncodedData() != null) {
            if (secondSignal.getEncodedData().booleanValue()) {
                return firstSignal;
            }
        }
        return null;
    }
}
