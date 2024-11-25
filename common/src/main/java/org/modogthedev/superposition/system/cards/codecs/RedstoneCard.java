package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

public class RedstoneCard extends Card {
    public RedstoneCard(ResourceLocation card) {
        super(card);
    }

    @Override
    public boolean modulateSignal(Signal signal) {
        signal.encode("Yooo wassup!");
        return super.modulateSignal(signal);
    }
}
