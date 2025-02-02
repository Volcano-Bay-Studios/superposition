package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class DistanceCard extends Card implements PeriphrealCard {
    public DistanceCard(ResourceLocation card) {
        super(card);
    }

    public DistanceCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal, Signal periphrealSignal) {

    }

    @Override
    public boolean encodeReturnValue() {
        return true;
    }

    @Override
    public Card copy() {
        return new DistanceCard(this);
    }

    @Override
    public void returnSignal(Signal signal, BlockEntity blockEntity) {
        if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity)
            signal.encode(analyserBlockEntity.distance);
    }
}
