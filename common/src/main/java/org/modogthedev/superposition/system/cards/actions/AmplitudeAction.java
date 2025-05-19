package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.MonoModifyAction;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SuperpositionMth;

public class AmplitudeAction extends Action implements MonoModifyAction {

    public AmplitudeAction(ResourceLocation card) {
        super(card);
    }

    @Override
    public Signal modify(Signal signal) {
        signal.encode(SuperpositionMth.mapAmplitude(signal.getAmplitude()));
        return signal;
    }
}
