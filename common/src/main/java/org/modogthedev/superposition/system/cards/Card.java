package org.modogthedev.superposition.system.cards;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.core.SuperpositionCards;
import org.modogthedev.superposition.core.SuperpositionFilters;
import org.modogthedev.superposition.system.cards.config.CardConfig;
import org.modogthedev.superposition.system.cards.config.CardConfigPart;
import org.modogthedev.superposition.system.signal.Signal;

public class Card {
    private ResourceLocation selfReference;
    public Card(ResourceLocation card) {
        this.selfReference = card;
    }
    public void save(CompoundTag pTag) {
        pTag.putString("namespace", selfReference.getNamespace());
        pTag.putString("path", selfReference.getPath());
    }
    public static Card loadNew(CompoundTag pTag) {
        Card card = SuperpositionCards.CARDS.getRegistrar().get(new ResourceLocation(pTag.getString("namespace"), pTag.getString("path")));
        if (card != null)
            card.load(pTag);
        return  card;
    }
    public void load(CompoundTag pTag) {

    }
    public ResourceLocation getSelfReference() {
        return this.selfReference;
    }
    public CardConfig runCardConfig(CardConfig config) {
        return config;
    }
    public CardConfig getCardConfig() {
        return runCardConfig(new CardConfig());
    }

    /**
     * @param signal
     * @return This boolean tells the computer whether it should throw away this signal
     */
    public boolean modulateSignal(Signal signal) {
        return false;
    }
}
