package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;

import java.io.Serializable;

public class StringCard extends Card {
    public StringCard(ResourceLocation card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal) {
        EncodedData<? extends Serializable> data = signal.getEncodedData();
        if (data != null)
            signal.encode(String.valueOf(data.getObj()));
    }
}
