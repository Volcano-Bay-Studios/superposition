package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class RedstoneCard extends Card implements PeripheralCard {

    public RedstoneCard(ResourceLocation card) {
        super(card);
    }

    public RedstoneCard(Card card) {
        super(card);
    }

    @Override
    public void peripheralEncode(Signal signal, BlockEntity blockEntity) {
        int value = 0;
        if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
            value = blockEntity.getLevel().getBestNeighborSignal(analyserBlockEntity.getAnalysisPosition());
        }
        signal.encode(value);
    }

    @Override
    public void affectBlock(Signal signal, Level level, BlockPos pos) {
        if (signal != null && signal.getEncodedData() != null) {
        }
    }

    @Override
    public Card copy() {
        return new RedstoneCard(this);
    }
}
