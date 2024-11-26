package org.modogthedev.superposition.core;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.item.*;

import java.awt.*;
import java.util.function.Supplier;

public class SuperpositionItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Superposition.MODID, Registries.ITEM);
    // TOOL / UTILITY
    public static final RegistrySupplier<ScrewdriverItem> SCREWDRIVER = registerItem("screwdriver", () -> new ScrewdriverItem(new Item.Properties().stacksTo(1).arch$tab(SuperpositionTabs.TAB)));
    public static final RegistrySupplier<CarabinerItem> CABLE_CARABINER = registerItem("cable_carabiner", () -> new CarabinerItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB)));
    public static final RegistrySupplier<CableItem> WHITE_CABLE = registerItem("white_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(249, 255, 254)));
    public static final RegistrySupplier<CableItem> LIGHT_GRAY_CABLE = registerItem("light_gray_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(157, 157, 151)));
    public static final RegistrySupplier<CableItem> GRAY_CABLE = registerItem("gray_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(71, 79, 82)));
    public static final RegistrySupplier<CableItem> BLACK_CABLE = registerItem("black_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(29, 29, 33)));
    public static final RegistrySupplier<CableItem> BROWN_CABLE = registerItem("brown_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(131, 84, 50)));
    public static final RegistrySupplier<CableItem> RED_CABLE = registerItem("red_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(176, 46, 38)));
    public static final RegistrySupplier<CableItem> ORANGE_CABLE = registerItem("orange_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(249, 128, 29)));
    public static final RegistrySupplier<CableItem> YELLOW_CABLE = registerItem("yellow_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(254, 216, 61)));
    public static final RegistrySupplier<CableItem> GREEN_CABLE = registerItem("green_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(94, 124, 22)));
    public static final RegistrySupplier<CableItem> LIME_CABLE = registerItem("lime_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(128, 199, 31)));
    public static final RegistrySupplier<CableItem> CYAN_CABLE = registerItem("cyan_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(22, 156, 156)));
    public static final RegistrySupplier<CableItem> LIGHT_BLUE_CABLE = registerItem("light_blue_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(58, 179, 218)));
    public static final RegistrySupplier<CableItem> BLUE_CABLE = registerItem("blue_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(60, 68, 170)));
    public static final RegistrySupplier<CableItem> PURPLE_CABLE = registerItem("purple_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(137, 50, 184)));
    public static final RegistrySupplier<CableItem> MAGENTA_CABLE = registerItem("magenta_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(199, 78, 189)));
    public static final RegistrySupplier<CableItem> PINK_CABLE = registerItem("pink_cable", () -> new CableItem(new Item.Properties().arch$tab(SuperpositionTabs.TAB),new Color(243, 139, 170)));
    // Filters
    public static final RegistrySupplier<FilterItem> HIGH_PASS_FILTER = registerItem("high_pass_filter", () -> new FilterItem(new FilterItem.Properties().type(SuperpositionFilters.HIGH_PASS.get()), new Item.Properties().stacksTo(1).arch$tab(SuperpositionTabs.TAB)));
    public static final RegistrySupplier<FilterItem> LOW_PASS_FILTER = registerItem("low_pass_filter", () -> new FilterItem(new FilterItem.Properties().type(SuperpositionFilters.LOW_PASS.get()), new Item.Properties().stacksTo(1).arch$tab(SuperpositionTabs.TAB)));
    public static final RegistrySupplier<FilterItem> BAND_PASS_FILTER = registerItem("band_pass_filter", () -> new FilterItem(new FilterItem.Properties().type(SuperpositionFilters.BAND_PASS.get()), new Item.Properties().stacksTo(1).arch$tab(SuperpositionTabs.TAB)));
    // Cards
    // CRAFTING
    public static final RegistrySupplier<Item> INSULATED_SHEET = registerItem("insulated_sheet", () -> new Item(new Item.Properties().arch$tab(SuperpositionTabs.TAB)));
    // BLOCK ITEM

    private static <T extends Item> RegistrySupplier<T> registerItem(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
