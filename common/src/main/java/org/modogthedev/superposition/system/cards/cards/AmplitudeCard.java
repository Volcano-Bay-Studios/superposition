package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SuperpositionMth;

public class AmplitudeCard extends Card {

    public AmplitudeCard(ResourceLocation card) {
        super(card);
    }

    public AmplitudeCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal, Signal periphrealSignal) {
        signal.encode(SuperpositionMth.mapAmplitude(signal.getAmplitude()));
    }

    @Override
    public Card copy() {
        return new AmplitudeCard(this);
    }
}
