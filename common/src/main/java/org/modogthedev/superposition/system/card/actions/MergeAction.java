package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.ArraySetExecutableAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class MergeAction extends ArraySetExecutableAction {

    public MergeAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    protected List<String> arrayKeys() {
        return List.of("tag1", "tag2");
    }

    @Override
    protected Signal executeArray(Signal[] signals) {
        Signal firstSignal = signals[0];
        Signal secondSignal = signals[1];

        if (firstSignal != null && firstSignal.getEncodedData() != null && secondSignal != null && secondSignal.getEncodedData() != null) {
            firstSignal.encode(firstSignal.getEncodedData().compoundTagData().merge(secondSignal.getEncodedData().compoundTagData()));
        }
        return firstSignal;
    }
}
