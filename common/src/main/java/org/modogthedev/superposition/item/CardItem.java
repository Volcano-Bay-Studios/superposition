package org.modogthedev.superposition.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.blockentity.FilterBlockEntity;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.screens.ScreenManager;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.filter.Filter;

public class CardItem extends Item {
    public Card card;

    public CardItem(CardItem.Properties pProperties, Item.Properties properties) {
        super(properties);
        this.card = pProperties.card;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getItemInHand().getItem() instanceof CardItem)
            card.load(context.getItemInHand().getTagElement("card"));
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof ComputerBlockEntity computerBlockEntity) {
            boolean creative = context.getPlayer().getAbilities().instabuild;
            if (computerBlockEntity.getCard() == null) {
                computerBlockEntity.setCard(card);
                if (!creative)
                    return InteractionResult.CONSUME;
                else
                    return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    public static class Properties {
        public Card card;

        public CardItem.Properties type(Card card) {
            this.card = card;
            return this;
        }
    }
}
