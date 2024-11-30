package org.modogthedev.superposition.core;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
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
    public static final DeferredRegister<Card> CARDS = DeferredRegister.create(Superposition.MODID, SuperpositionRegistries.CARD_REGISTRY);
    public static final RegistrySupplier<Card> REDSTONE_CARD = registerCard("redstone_card", () -> new RedstoneCard(Superposition.id("redstone_card")));
    public static final RegistrySupplier<Card> STRING_CARD = registerCard("string_card", () -> new StringCard(Superposition.id("string_card")));
    public static final RegistrySupplier<Card> SIGN_CARD = registerCard("sign_card", () -> new SignCard(Superposition.id("sign_card")));
    public static final RegistrySupplier<Card> CONTAINER_CARD = registerCard("container_card", () -> new ContainerCard(Superposition.id("container_card")));

    private static <T extends Card> RegistrySupplier<T> registerCard(String name, Supplier<T> card) {
        SuperpositionItems.ITEMS.register(name, () -> new CardItem(new CardItem.Properties().type(card.get()),new Item.Properties().stacksTo(1).arch$tab(SuperpositionTabs.TAB))); // Does the supplier work?
        return CARDS.register(name, card);
    }
}
