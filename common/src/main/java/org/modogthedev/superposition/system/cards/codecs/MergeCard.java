package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class MergeCard extends Card {

    public MergeCard(ResourceLocation card) {
        super(card);
    }

    public MergeCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal, Signal periphrealSignal) {
        if (periphrealSignal != null && signal.getEncodedData() != null && periphrealSignal.getEncodedData() != null && periphrealSignal.getEncodedData().compoundTagData() != null && signal.getEncodedData().compoundTagData() != null) {
            signal.encode(signal.getEncodedData().compoundTagData().merge(periphrealSignal.getEncodedData().compoundTagData()));
        }
    }

    @Override
    public Card copy() {
        return new MergeCard(this);
    }
}
