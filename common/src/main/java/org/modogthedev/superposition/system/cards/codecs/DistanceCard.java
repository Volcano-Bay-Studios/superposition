package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class DistanceCard extends Card {
    public DistanceCard(ResourceLocation card) {
        super(card);
    }

    public DistanceCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal, Signal periphrealSignal) {
        if (periphrealBlockEntity instanceof AnalyserBlockEntity analyserBlockEntity)
            signal.encode(analyserBlockEntity.distance);
    }

    @Override
    public boolean requiresPeriphreal() {
        return true;
    }

    @Override
    public Card copy() {
        return new DistanceCard(this);
    }
}
