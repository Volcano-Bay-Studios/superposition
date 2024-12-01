package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.item.CardItem;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.cards.codecs.ContainerCard;
import org.modogthedev.superposition.system.cards.codecs.RedstoneCard;
import org.modogthedev.superposition.system.cards.codecs.SignCard;
import org.modogthedev.superposition.system.cards.codecs.StringCard;

import java.util.function.Supplier;

public class SuperpositionCards {

    public static final RegistrationProvider<Card> CARDS = RegistrationProvider.get(ResourceKey.createRegistryKey(Superposition.id("card")), Superposition.MODID);
    public static final RegistryObject<Card> REDSTONE_CARD = registerCard("redstone_card", () -> new RedstoneCard(Superposition.id("redstone_card")));
    public static final RegistryObject<Card> STRING_CARD = registerCard("string_card", () -> new StringCard(Superposition.id("string_card")));
    public static final RegistryObject<Card> SIGN_CARD = registerCard("sign_card", () -> new SignCard(Superposition.id("sign_card")));
    public static final RegistryObject<Card> CONTAINER_CARD = registerCard("container_card", () -> new ContainerCard(Superposition.id("container_card")));

    private static <T extends Card> RegistryObject<T> registerCard(String name, Supplier<T> card) {
        SuperpositionItems.ITEMS.register(name, () -> new CardItem(new CardItem.Properties().type(card.get()), new Item.Properties().stacksTo(1))); // Does the supplier work?
        return CARDS.register(name, card);
    }

    public static void bootstrap() {
    }
}
