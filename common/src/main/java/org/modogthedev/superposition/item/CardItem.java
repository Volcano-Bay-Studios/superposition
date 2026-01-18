package org.modogthedev.superposition.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.blockentity.util.CardHolder;
import org.modogthedev.superposition.system.card.Card;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

public class CardItem extends Item {
    public Card card;

    public CardItem(Item.Properties properties) {
        super(properties);
    }

    public void runIfHasData(ItemStack stack, Consumer<CompoundTag> tagConsumer) {
        CompoundTag tag = getTagElement(stack);
        if (tag != null) {
            tagConsumer.accept(tag);
        }
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack itemStack = super.getDefaultInstance();
        CompoundTag cardData = new CompoundTag();
        CompoundTag tag = new CompoundTag();
        tag.put("card", new Card().save(cardData));
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return itemStack;
    }


    public Card getCard(ItemStack stack) {
        CompoundTag tag = getTagElement(stack);
        if (tag != null) {
            return new Card(tag);
        } else {
            return null;
        }
    }

    public void putData(ItemStack stack, CompoundTag cardData) {
        CompoundTag tag = new CompoundTag();
        tag.put("card", cardData);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public ItemStack create(Card card) {
        ItemStack itemStack = getDefaultInstance();
        CompoundTag cardData = new CompoundTag();
        card.save(cardData);
        CompoundTag tag = new CompoundTag();
        tag.put("card", cardData);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return itemStack;
    }

    public CompoundTag getTagElement(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyTag().getCompound("card");
        }
        return null;
    }

    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = getTagElement(stack);
        if (tag != null) {
            return Component.literal(tag.getString("title"));
        } else {
            return Component.translatable("item.superposition.card");
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getItemInHand().getItem() instanceof CardItem) {
            card = null;
            runIfHasData(context.getItemInHand(), (a) -> {
                System.out.println(a.toString());
                card = new Card(a);
            });
            if (card == null) {
                card = new Card();
            }
            if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof CardHolder cardHolder) {
                boolean creative = context.getPlayer().getAbilities().instabuild;
                if (cardHolder.getCard() == null) {
                    cardHolder.setCard(card);
                    context.getItemInHand().setCount(0);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (stack.getItem() instanceof CardItem) {
            Card card = getCard(stack);
            if (card != null) {
                String text = card.save(new CompoundTag()).toString();
                MutableComponent component;
                component = Component.literal(text.getBytes(StandardCharsets.UTF_8).length + " Bytes");
                component.setStyle(Style.EMPTY.withColor(Superposition.SUPERPOSITION_THEME.get("topBorder")));
                tooltipComponents.add(component);
            }
        }
    }
}
