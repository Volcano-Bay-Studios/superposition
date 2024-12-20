package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.item.CardItem;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.cards.codecs.*;

import java.util.function.Supplier;

public class SuperpositionCards {

    public static final RegistrationProvider<Card> CARDS = RegistrationProvider.get(ResourceKey.createRegistryKey(Superposition.id("card")), Superposition.MODID);
    public static final RegistryObject<Card> SIGN_CARD = registerCard("sign_card", () -> new SignCard(Superposition.id("sign_card")));
    public static final RegistryObject<Card> REDSTONE_CARD = registerCard("redstone_card", () -> new RedstoneCard(Superposition.id("redstone_card")));
    public static final RegistryObject<Card> CONTAINER_CARD = registerCard("container_card", () -> new ContainerCard(Superposition.id("container_card")));
    public static final RegistryObject<Card> IDENTITY_CARD = registerCard("identity_card", () -> new IdentityCard(Superposition.id("identity_card")));
    public static final RegistryObject<Card> DISTANCE_CARD = registerCard("distance_card", () -> new DistanceCard(Superposition.id("distance_card")));

    private static <T extends Card> RegistryObject<T> registerCard(String name, Supplier<T> card) {
        SuperpositionItems.registerItem(name, () -> new CardItem(new CardItem.Properties().type(card.get()), new Item.Properties().stacksTo(1))); // Does the supplier work?
        return CARDS.register(name, card);
    }

    public static void bootstrap() {
    }
}
