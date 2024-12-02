package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.platform.PlatformHelper;

public class SuperpositionTabs {

    public static final RegistrationProvider<CreativeModeTab> TABS = RegistrationProvider.get(Registries.CREATIVE_MODE_TAB, Superposition.MODID);
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("tab",
            () -> PlatformHelper.INSTANCE.creativeTabBuilder()
                    .title(Component.translatable("creativemodetab." + Superposition.MODID))
                    .icon(() -> new ItemStack(SuperpositionItems.LIME_CABLE.get()))
                    .displayItems(SuperpositionItems::fillTab)
                    .build());

    public static void bootstrap() {
    }
}