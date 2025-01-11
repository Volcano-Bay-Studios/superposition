package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class AmplitudeCard extends Card {

    public AmplitudeCard(ResourceLocation card) {
        super(card);
    }

    public AmplitudeCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal) {
        signal.encode(signal.getAmplitude());
    }

    @Override
    public Card copy() {
        return new AmplitudeCard(this);
    }
}
