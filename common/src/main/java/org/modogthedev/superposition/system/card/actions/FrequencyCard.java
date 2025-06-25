package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.MonoModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class FrequencyCard extends Action implements MonoModifyAction {

    public FrequencyCard(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal signal) {
        signal.encode(signal.getFrequency());
        return signal;
    }
}