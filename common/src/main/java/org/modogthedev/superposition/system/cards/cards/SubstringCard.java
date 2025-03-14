package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class SubstringCard extends Card {

    public SubstringCard(ResourceLocation card) {
        super(card);
    }

    public SubstringCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal, Signal periphrealSignal) {
        if (periphrealSignal != null && signal.getEncodedData() != null && periphrealSignal.getEncodedData() != null) {
            String s = signal.getEncodedData().stringValue();
            int cutPosition = periphrealSignal.getEncodedData().intValue();
            if (s != null) {
                if (cutPosition >= 0) {
                    signal.encode(s.substring(0, Math.max(0, s.length() - cutPosition)));
                } else {
                    signal.encode(s.substring(-cutPosition));
                }
            }
        }
    }

    @Override
    public Card copy() {
        return new SubstringCard(this);
    }
}
