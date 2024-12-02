package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class SignCard extends Card {
    public SignCard(ResourceLocation card) {
        super(card);
    }

    public SignCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal) {
        String text = "";
        if (periphrealBlockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
            BlockEntity blockEntity1 = periphrealBlockEntity.getLevel().getBlockEntity(analyserBlockEntity.getAnalysisPosition());
            if (blockEntity1 instanceof SignBlockEntity signBlockEntity) {
                for (Component component : signBlockEntity.getFrontText().getMessages(true)) {
                    if (!component.getString().isEmpty())
                        text = text.concat((text.isEmpty() ? "" : " ") + component.getString());
                }
                for (Component component : signBlockEntity.getBackText().getMessages(true)) {
                    if (!component.getString().isEmpty())
                        text = text.concat((text.isEmpty() ? "" : " ") + component.getString());
                }
            }
        }
        if (!text.isEmpty())
            signal.encode(text);
    }

    @Override
    public Card copy() {
        return new SignCard(this);
    }
}
