package org.modogthedev.superposition.item;

import net.minecraft.world.item.Item;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.filter.Filter;

public class CardItem extends Item {
    public Card card;
    public CardItem(CardItem.Properties pProperties, Item.Properties properties) {
        super(properties);
        this.card = pProperties.card;
    }

    public static class Properties {
        public Card card;

        public CardItem.Properties type(Card card) {
            this.card = card;
            return this;
        }
    }
}
