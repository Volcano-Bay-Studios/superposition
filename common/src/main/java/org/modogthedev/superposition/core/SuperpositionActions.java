package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.resources.ResourceKey;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.compat.CompatabilityHandler;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.actions.*;

import java.util.function.Supplier;

public class SuperpositionActions {

    public static final RegistrationProvider<Action> ACTION = RegistrationProvider.get(ResourceKey.createRegistryKey(Superposition.id("action")), Superposition.MODID);
    public static final RegistryObject<Action> COLOR_CARD = registerAction("color_card", () -> new ColorAction(Superposition.id("color")));
    public static final RegistryObject<Action> SIGN_CARD = registerAction("sign_card", () -> new SignCard(Superposition.id("sign_card")));
    public static final RegistryObject<Action> REDSTONE_CARD = registerAction("redstone_card", () -> new RedstoneCard(Superposition.id("redstone_card")));
    public static final RegistryObject<Action> CONTAINER_CARD = registerAction("container_card", () -> new ContainerAction(Superposition.id("container_card")));
    public static final RegistryObject<Action> IDENTITY_CARD = registerAction("identity_card", () -> new IdentityCard(Superposition.id("identity_card")));
    public static final RegistryObject<Action> TEMPERATURE_CARD = registerAction("temperature_card", () -> new TemperatureCard(Superposition.id("temperature_card")));
    public static final RegistryObject<Action> DISTANCE_CARD = registerAction("distance_card", () -> new DistanceAction(Superposition.id("distance_card")));
    public static final RegistryObject<Action> AMPLITUDE_CARD = registerAction("amplitude_card", () -> new AmplitudeAction(Superposition.id("amplitude_card")));
    public static final RegistryObject<Action> FREQUENCY_CARD = registerAction("frequency_card", () -> new FrequencyCard(Superposition.id("frequency_card")));
    public static final RegistryObject<Action> ENCAPSULATE_CARD = registerAction("encapsulate_card", () -> new EncapsulateAction(Superposition.id("encapsulate_card")));
    public static final RegistryObject<Action> MERGE_CARD = registerAction("merge_card", () -> new MergeAction(Superposition.id("merge_card")));
    public static final RegistryObject<Action> RETRIEVE_CARD = registerAction("retrieve_card", () -> new RetriveCard(Superposition.id("retrieve_card")));
    public static final RegistryObject<Action> SUBSTRING_CARD = registerAction("substring_card", () -> new SubstringCard(Superposition.id("substring_card")));
    public static final RegistryObject<Action> SLAVE_CARD = registerAction("slave_card", () -> new SlaveCard(Superposition.id("slave_card")),CompatabilityHandler.Mod.COMPUTERCRAFT);

    private static <T extends Action> RegistryObject<T> registerAction(String name, Supplier<T> action) {
        return ACTION.register(name, action);
    }

    private static <T extends Action> RegistryObject<T> registerAction(String name, Supplier<T> action, CompatabilityHandler.Mod mod) {
        return ACTION.register(name, action);
    }

    public static void bootstrap() {
    }
}
