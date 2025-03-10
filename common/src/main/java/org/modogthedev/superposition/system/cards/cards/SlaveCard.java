package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;

public class SlaveCard extends Card implements SynchronizedCard {

    public SlaveCard(ResourceLocation card) {
        super(card);
    }

    public SlaveCard(Card card) {
        super(card);
    }

    public EncodedData<?> encodedData;

    @Override
    public void modulateSignal(Signal signal, Signal periphrealSignal) {
        if (signal != null && encodedData != null) {
            signal.setEncodedData(encodedData);
        }
    }

    public EncodedData<?> getEncodedData() {
        return encodedData;
    }

    public void setEncodedData(EncodedData<?> encodedData) {
        this.encodedData = encodedData;
    }

    @Override
    public Card copy() {
        return new SlaveCard(this);
    }
}
