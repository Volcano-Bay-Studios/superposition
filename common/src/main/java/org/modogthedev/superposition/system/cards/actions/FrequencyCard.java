package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.MonoModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class FrequencyCard extends Action implements MonoModifyAction {

    public FrequencyCard(ResourceLocation card) {
        super(card);
    }


    @Override
    public Signal modify(Signal signal) {
        signal.encode(signal.getFrequency());
        return signal;
    }
}