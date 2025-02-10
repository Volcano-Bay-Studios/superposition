package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class FrequencyCard extends Card {

    public FrequencyCard(ResourceLocation card) {
        super(card);
    }

    public FrequencyCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal, Signal periphrealSignal) {
        signal.encode(signal.getFrequency());
    }

    @Override
    public Card copy() {
        return new FrequencyCard(this);
    }
}