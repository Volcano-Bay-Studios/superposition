package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AmplifierBlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class TemperatureCard extends Card implements PeripheralCard {
    public TemperatureCard(ResourceLocation card) {
        super(card);
    }

    public TemperatureCard(Card card) {
        super(card);
    }

    @Override
    public void peripheralEncode(Signal signal, BlockEntity blockEntity) {
        if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
            BlockEntity blockEntity1 = blockEntity.getLevel().getBlockEntity(analyserBlockEntity.getAnalysisPosition());
            if (blockEntity1 instanceof AmplifierBlockEntity amplifierBlockEntity) {
                signal.encode(amplifierBlockEntity.temp);
            }
        }
    }


    @Override
    public Card copy() {
        return new TemperatureCard(this);
    }
}
