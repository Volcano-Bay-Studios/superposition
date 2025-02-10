package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class IdentityCard extends Card implements PeriphrealCard {
    public IdentityCard(ResourceLocation card) {
        super(card);
    }

    public IdentityCard(Card card) {
        super(card);
    }

    @Override
    public void returnSignal(Signal signal, BlockEntity blockEntity) {
        if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity)
            signal.encode(analyserBlockEntity.getLevel().getBlockState(analyserBlockEntity.getAnalysisPosition()).getBlock().getName().getString());
    }

    @Override
    public boolean encodeReturnValue() {
        return true;
    }

    @Override
    public Card copy() {
        return new IdentityCard(this);
    }
}
