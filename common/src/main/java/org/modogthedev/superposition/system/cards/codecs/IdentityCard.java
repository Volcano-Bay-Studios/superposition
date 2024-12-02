package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class IdentityCard extends Card {
    public IdentityCard(ResourceLocation card) {
        super(card);
    }

    public IdentityCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal) {
        if (periphrealBlockEntity instanceof AnalyserBlockEntity analyserBlockEntity)
            signal.encode(analyserBlockEntity.getLevel().getBlockState(analyserBlockEntity.getAnalysisPosition()).getBlock().getName().getString());
    }

    @Override
    public boolean requiresPeriphreal() {
        return true;
    }

    @Override
    public Card copy() {
        return new IdentityCard(this);
    }
}
