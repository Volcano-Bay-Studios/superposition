package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.compat.CompatabilityHandler;
import org.modogthedev.superposition.item.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SuperpositionItems {

    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, Superposition.MODID);
    private static final List<RegistryObject<? extends Item>> ITEM_ORDER = new ArrayList<>();

    // TOOL / UTILITY
    public static final RegistryObject<ScrewdriverItem> SCREWDRIVER = registerItem("screwdriver", () -> new ScrewdriverItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<SignalScopeItem> SIGNAL_SCOPE = registerItem("signal_scope", () -> new SignalScopeItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<CarabinerItem> CABLE_CARABINER = registerItem("cable_carabiner", () -> new CarabinerItem(new Item.Properties()));
    public static final RegistryObject<CableItem> GLOWING_CABLE = registerItem("glowing_cable", () -> new CableItem(new Item.Properties(), new Color(249, 255, 254), true));
    public static final RegistryObject<CableItem> WHITE_CABLE = registerItem("white_cable", () -> new CableItem(new Item.Properties(), new Color(249, 255, 254), false));
    public static final RegistryObject<CableItem> LIGHT_GRAY_CABLE = registerItem("light_gray_cable", () -> new CableItem(new Item.Properties(), new Color(157, 157, 151), false));
    public static final RegistryObject<CableItem> GRAY_CABLE = registerItem("gray_cable", () -> new CableItem(new Item.Properties(), new Color(71, 79, 82), false));
    public static final RegistryObject<CableItem> BLACK_CABLE = registerItem("black_cable", () -> new CableItem(new Item.Properties(), new Color(29, 29, 33), false));
    public static final RegistryObject<CableItem> BROWN_CABLE = registerItem("brown_cable", () -> new CableItem(new Item.Properties(), new Color(131, 84, 50), false));
    public static final RegistryObject<CableItem> RED_CABLE = registerItem("red_cable", () -> new CableItem(new Item.Properties(), new Color(176, 46, 38), false));
    public static final RegistryObject<CableItem> ORANGE_CABLE = registerItem("orange_cable", () -> new CableItem(new Item.Properties(), new Color(249, 128, 29), false));
    public static final RegistryObject<CableItem> YELLOW_CABLE = registerItem("yellow_cable", () -> new CableItem(new Item.Properties(), new Color(254, 216, 61), false));
    public static final RegistryObject<CableItem> GREEN_CABLE = registerItem("green_cable", () -> new CableItem(new Item.Properties(), new Color(94, 124, 22), false));
    public static final RegistryObject<CableItem> LIME_CABLE = registerItem("lime_cable", () -> new CableItem(new Item.Properties(), new Color(128, 199, 31), false));
    public static final RegistryObject<CableItem> CYAN_CABLE = registerItem("cyan_cable", () -> new CableItem(new Item.Properties(), new Color(22, 156, 156), false));
    public static final RegistryObject<CableItem> LIGHT_BLUE_CABLE = registerItem("light_blue_cable", () -> new CableItem(new Item.Properties(), new Color(58, 179, 218), false));
    public static final RegistryObject<CableItem> BLUE_CABLE = registerItem("blue_cable", () -> new CableItem(new Item.Properties(), new Color(60, 68, 170), false));
    public static final RegistryObject<CableItem> PURPLE_CABLE = registerItem("purple_cable", () -> new CableItem(new Item.Properties(), new Color(137, 50, 184), false));
    public static final RegistryObject<CableItem> MAGENTA_CABLE = registerItem("magenta_cable", () -> new CableItem(new Item.Properties(), new Color(199, 78, 189), false));
    public static final RegistryObject<CableItem> PINK_CABLE = registerItem("pink_cable", () -> new CableItem(new Item.Properties(), new Color(243, 139, 170), false));
    // Filters
    public static final RegistryObject<FilterItem> HIGH_PASS_FILTER = registerItem("high_pass_filter", () -> new FilterItem(SuperpositionFilters.HIGH_PASS, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<FilterItem> LOW_PASS_FILTER = registerItem("low_pass_filter", () -> new FilterItem(SuperpositionFilters.LOW_PASS, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<FilterItem> BAND_PASS_FILTER = registerItem("band_pass_filter", () -> new FilterItem(SuperpositionFilters.BAND_PASS, new Item.Properties().stacksTo(1)));
    // Cards
    public static final RegistryObject<CardItem> CARD = registerItem("card", () -> new CardItem(new Item.Properties().stacksTo(1)));
    // CRAFTING
    public static final RegistryObject<Item> PLIANT_SHEET = registerItem("pliant_sheet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INTEGRATED_CIRCUIT = registerItem("integrated_circuit", () -> new Item(new Item.Properties()));
    // BLOCK ITEM

    public static void fillTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        for (RegistryObject<? extends Item> object : ITEM_ORDER) {
            output.accept(object.get());
        }
    }

    public static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item) {
        RegistryObject<T> object = ITEMS.register(name, item);
        ITEM_ORDER.add(object);
        return object;
    }

    public static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item, CompatabilityHandler.Mod mod) {
        RegistryObject<T> object = ITEMS.register(name, item);
        if (mod.isLoaded) {
            ITEM_ORDER.add(object);
        }
        return object;
    }

    public static void bootstrap() {
    }
}
