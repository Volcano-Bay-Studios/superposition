package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class RedstoneCard extends Card {

    public RedstoneCard(ResourceLocation card) {
        super(card);
    }

    public RedstoneCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal) {
        int value = 0;
        if (periphrealBlockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
            value = periphrealBlockEntity.getLevel().getBestNeighborSignal(analyserBlockEntity.getAnalysisPosition());
        }
        signal.encode(value);
    }


    @Override
    public boolean requiresPeriphreal() {
        return true;
    }

    @Override
    public Card copy() {
        return new RedstoneCard(this);
    }
}
