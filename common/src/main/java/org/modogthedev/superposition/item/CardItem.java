package org.modogthedev.superposition.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.system.cards.Card;

import java.util.function.Consumer;

public class CardItem extends Item {
    public Card card;

    public CardItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = super.getDefaultInstance();
        CompoundTag tag = new CompoundTag();
        card.save(tag);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return super.getDefaultInstance();
    }

    public void runIfHasData(ItemStack stack, Consumer<CompoundTag> tagConsumer) {
        CompoundTag tag = getTagElement(stack);
        if (tag != null) {
            tagConsumer.accept(tag);
        }
    }

    public Card getCard(ItemStack stack) {
        CompoundTag tag = getTagElement(stack);
        return new Card(tag);
    }

    public CompoundTag getTagElement(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyTag();
        }
        return null;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getItemInHand().getItem() instanceof CardItem)
            runIfHasData(context.getItemInHand(), (a) -> {
                card.load(a);
            });
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof ComputerBlockEntity computerBlockEntity) {
            boolean creative = context.getPlayer().getAbilities().instabuild;
            if (computerBlockEntity.getCard() == null) {
                if (!creative)
                    return InteractionResult.CONSUME;
                else
                    return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }
}
