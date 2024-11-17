package org.modogthedev.superposition.core;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.item.CardItem;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.filter.BandPassFilter;
import org.modogthedev.superposition.system.filter.Filter;
import org.modogthedev.superposition.system.filter.HighPassFilter;
import org.modogthedev.superposition.system.filter.LowPassFilter;

import java.util.function.Supplier;

public class SuperpositionCards {
    public static final DeferredRegister<Card> CARDS = DeferredRegister.create(Superposition.MODID, SuperpositionRegistries.CARD_REGISTRY);
    public static final RegistrySupplier<Card> REDSTONE_CARD = registerCard("redstone_card", () -> new Card(Superposition.id("redstone_card")));

    private static <T extends Card> RegistrySupplier<T> registerCard(String name, Supplier<T> filter) {
        SuperpositionItems.ITEMS.register(name, () -> new CardItem(new CardItem.Properties().type(filter.get()),new Item.Properties().stacksTo(1).arch$tab(SuperpositionTabs.TAB))); // Does the supplier work?
        return CARDS.register(name, filter);
    }
}
