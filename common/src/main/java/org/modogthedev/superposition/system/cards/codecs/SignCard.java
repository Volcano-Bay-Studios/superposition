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
        String orignalText = "";
        if (signal.getEncodedData() != null && signal.getEncodedData().value() instanceof String text) {
            orignalText = text;
        }
        if (peripherialPosition != null) {
            BlockEntity blockEntity = computerBlockEntity.getLevel().getBlockEntity(peripherialPosition);
            if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
                BlockEntity blockEntity1 = computerBlockEntity.getLevel().getBlockEntity(analyserBlockEntity.getAnalysisPosition());
                if (blockEntity1 instanceof SignBlockEntity signBlockEntity) {
                    for (Component component : signBlockEntity.getFrontText().getMessages(true)) {
                        if (!component.getString().isEmpty()) {
                            orignalText = orignalText.concat((orignalText.isEmpty() ? "" : " ") + component.getString());
                        }
                    }
                    for (Component component : signBlockEntity.getBackText().getMessages(true)) {
                        if (!component.getString().isEmpty()) {
                            orignalText = orignalText.concat((orignalText.isEmpty() ? "" : " ") + component.getString());
                        }
                    }
                }
            }
        }
        if (!orignalText.isEmpty()) {
            signal.encode(orignalText);
        }
    }

    @Override
    public Card copy() {
        return new SignCard(this);
    }
}
