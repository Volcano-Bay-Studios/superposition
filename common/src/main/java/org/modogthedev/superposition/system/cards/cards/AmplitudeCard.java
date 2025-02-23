package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
    public void modulateSignal(Signal signal, Signal periphrealSignal) {
        signal.encode(Mth.map(signal.getAmplitude(), 0, 153, 3, 10));
    }

    @Override
    public Card copy() {
        return new AmplitudeCard(this);
    }
}
