package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.card.ArraySetExecutableAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class RetrieveAction extends ArraySetExecutableAction {

    public RetrieveAction(ResourceLocation action, Information info) {
        super(action, info);
    }


    @Override
    protected List<String> arrayKeys() {
        return List.of("Data", "Key");
    }

    @Override
    protected Signal executeArray(Signal[] signals) {
        Signal firstSignal = signals[0];
        Signal secondSignal = signals[1];

        if (secondSignal != null && firstSignal.getEncodedData() != null && secondSignal.getEncodedData() != null) {
            firstSignal.setEncodedData(firstSignal.getEncodedData().getTagKey(secondSignal.getEncodedData().stringValue()));
        }
        return firstSignal;

    }
}
