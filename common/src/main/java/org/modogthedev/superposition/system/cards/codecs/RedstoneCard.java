package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
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
    public void modulateSignal(Signal signal, Signal periphrealSignal) {
        int value = 0;
        if (periphrealBlockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
            value = periphrealBlockEntity.getLevel().getBestNeighborSignal(analyserBlockEntity.getAnalysisPosition());
        }
        signal.encode(value);
    }

    @Override
    public void affectBlock(Signal signal, Level level, BlockPos pos) {
        if (signal != null && signal.getEncodedData() != null) {
        }
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
