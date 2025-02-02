package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class RetriveCard extends Card {

    public RetriveCard(ResourceLocation card) {
        super(card);
    }

    public RetriveCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal, Signal periphrealSignal) {
        if (periphrealSignal != null && signal.getEncodedData() != null && periphrealSignal.getEncodedData() != null) {
            signal.setEncodedData(signal.getEncodedData().getTagKey(periphrealSignal.getEncodedData().stringValue()));
        }
    }

    @Override
    public Card copy() {
        return new RetriveCard(this);
    }
}
