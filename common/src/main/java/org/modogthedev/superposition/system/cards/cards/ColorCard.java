package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class ColorCard extends Card implements PeriphrealCard {
    public ColorCard(ResourceLocation card) {
        super(card);
    }

    public ColorCard(Card card) {
        super(card);
    }

    @Override
    public void returnSignal(Signal signal, BlockEntity blockEntity) {
        if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity)
            signal.encode(analyserBlockEntity.getLevel().getBlockState(analyserBlockEntity.getAnalysisPosition()).getBlock().defaultMapColor().col);
    }

    @Override
    public boolean encodeReturnValue() {
        return true;
    }

    @Override
    public Card copy() {
        return new ColorCard(this);
    }
}
