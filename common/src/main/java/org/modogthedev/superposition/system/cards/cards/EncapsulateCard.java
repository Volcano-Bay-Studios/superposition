package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class EncapsulateCard extends Card {

    public EncapsulateCard(ResourceLocation card) {
        super(card);
    }

    public EncapsulateCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal, Signal periphrealSignal) {
        if (signal.getEncodedData() != null) {
            CompoundTag tag = new CompoundTag();
            String key = "0";
            if (periphrealSignal != null && periphrealSignal.getEncodedData() != null) {
                key = periphrealSignal.getEncodedData().stringValue();
            }
            tag.putString(key,signal.getEncodedData().stringValue());
            signal.encode(tag);
        } else {
            signal.encode(new CompoundTag());
        }
    }

    @Override
    public Card copy() {
        return new EncapsulateCard(this);
    }
}
