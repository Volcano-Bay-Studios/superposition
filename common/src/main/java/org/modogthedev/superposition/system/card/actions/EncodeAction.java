package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.ArraySetExecutableAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class EncodeAction extends ArraySetExecutableAction {

    public EncodeAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    protected List<String> arrayKeys() {
        return List.of("signal", "data");
    }

    @Override
    protected Signal executeArray(Signal[] signals) {
        Signal firstSignal = signals[0];
        Signal secondSignal = signals[1];

        if (firstSignal != null && secondSignal != null && secondSignal.getEncodedData() != null) {
            firstSignal.setEncodedData(secondSignal.getEncodedData());
        }
        return firstSignal;
    }
}
