package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class RedstoneCard extends Card {
    public RedstoneCard(ResourceLocation card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal) {
        int value = 0;
        if (peripherialPosition != null) {
            BlockEntity blockEntity = computerBlockEntity.getLevel().getBlockEntity(peripherialPosition);
            if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
                value = computerBlockEntity.getLevel().getBestNeighborSignal(analyserBlockEntity.getAnalysisPosition());
            }
        }
        signal.encode(value);
    }
}
