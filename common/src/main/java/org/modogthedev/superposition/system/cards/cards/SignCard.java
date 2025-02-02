package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
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
    public void modulateSignal(Signal signal, Signal periphrealSignal) {
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
    public void affectBlock(Signal signal, Level level, BlockPos pos) {
        if (signal != null && signal.getEncodedData() != null) {
            BlockEntity blockEntity = periphrealBlockEntity.getLevel().getBlockEntity(pos);
            if (blockEntity instanceof SignBlockEntity signBlockEntity) {
                signBlockEntity.setText(new SignText().setMessage(0,Component.literal(signal.getEncodedData().stringValue())),true);
            }
        }
    }

    @Override
    public Card copy() {
        return new SignCard(this);
    }
}
