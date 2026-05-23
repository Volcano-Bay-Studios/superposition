package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.SingleExecutableAction;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SuperpositionMth;

public class AmplitudeAction extends SingleExecutableAction {

    public AmplitudeAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal signal) {
        signal.encode(SuperpositionMth.mapAmplitude(signal.getAmplitude()));
        return signal;
    }
}
