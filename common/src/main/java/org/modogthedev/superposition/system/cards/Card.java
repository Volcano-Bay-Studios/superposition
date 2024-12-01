package org.modogthedev.superposition.system.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.core.SuperpositionCards;
import org.modogthedev.superposition.system.cards.config.CardConfig;
import org.modogthedev.superposition.system.signal.Signal;

public abstract class Card {

    private final ResourceLocation selfReference;
    public ComputerBlockEntity computerBlockEntity;
    public BlockPos peripherialPosition;
    public int timeSincePeriphrealUpdated = 0;

    public Card(ResourceLocation card) {
        this.selfReference = card;
    }

    public Card(Card card) {
        this.selfReference = card.selfReference;
    }

    public static Card loadNew(CompoundTag pTag) {
        Card card = SuperpositionCards.CARDS.asVanillaRegistry().get(ResourceLocation.fromNamespaceAndPath(pTag.getString("namespace"), pTag.getString("path")));
        if (card != null) {
            card.load(pTag);
        }
        return card;
    }

    public void save(CompoundTag pTag) {
        pTag.putString("namespace", selfReference.getNamespace());
        pTag.putString("path", selfReference.getPath());
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

    public abstract Card copy();

    /**
     * @param signal
     * @return This boolean tells the computer whether it should throw away this signal
     */
    public void modulateSignal(Signal signal) {
    }
}
