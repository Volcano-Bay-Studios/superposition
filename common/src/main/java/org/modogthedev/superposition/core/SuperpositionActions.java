package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.compat.CompatabilityHandler;
import org.modogthedev.superposition.screens.utils.ActionSpritesheet;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.actions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SuperpositionActions {

    private static final List<Action> actions = new ArrayList<>();

    public static final ActionSpritesheet SPRITESHEET = new ActionSpritesheet(Superposition.id("textures/screen/action_spritesheet.png"),256);

    public static final RegistrationProvider<Action> ACTION = RegistrationProvider.get(ResourceKey.createRegistryKey(Superposition.id("action")), Superposition.MODID);
    public static final RegistryObject<Action> COLOR_CARD = registerAction("color_card", () -> new ColorAction(Superposition.id("color"), new Action.Information(
            Component.literal("Color"),
            Component.literal("Something about this card being cool or smth like that")
    )));
    public static final RegistryObject<Action> SIGN_CARD = registerAction("sign_card", () -> new SignCard(Superposition.id("sign_card"), new Action.Information(
            Component.literal("Sign"),
            Component.literal("Something about this card being cool or smth like that")
    )));
    public static final RegistryObject<Action> REDSTONE_CARD = registerAction("redstone_card", () -> new RedstoneCard(Superposition.id("redstone_card"), new Action.Information(
            Component.literal("Redstone"),
            Component.literal("Something about this card being cool or smth like that, as well as some VERY long text to help myself test how far the text can go, along with testing just how far the scrolling can go and to help me ")
    )));
    public static final RegistryObject<Action> CONTAINER_CARD = registerAction("container_card", () -> new ContainerAction(Superposition.id("container_card"), new Action.Information(
            Component.literal("Container"),
            Component.literal("Something about this card being cool or smth like that")
    )));
    public static final RegistryObject<Action> IDENTITY_CARD = registerAction("identity_card", () -> new IdentityCard(Superposition.id("identity_card"), new Action.Information(
            Component.literal("Identity"),
            Component.literal("Something about this card being cool or smth like that")
    )));
    public static final RegistryObject<Action> TEMPERATURE_CARD = registerAction("temperature_card", () -> new TemperatureCard(Superposition.id("temperature_card"), new Action.Information(
            Component.literal("Temperature"),
            Component.literal("Something about this card being cool or smth like that")
    )));
    public static final RegistryObject<Action> DISTANCE_CARD = registerAction("distance_card", () -> new DistanceAction(Superposition.id("distance_card"), new Action.Information(
            Component.literal("Distance"),
            Component.literal("Something about this card being cool or smth like that")
    )));
    public static final RegistryObject<Action> AMPLITUDE_CARD = registerAction("amplitude_card", () -> new AmplitudeAction(Superposition.id("amplitude_card"), new Action.Information(
            Component.literal("Amplitude"),
            Component.literal("Something about this card being cool or smth like that")
    )));
    public static final RegistryObject<Action> FREQUENCY_CARD = registerAction("frequency_card", () -> new FrequencyCard(Superposition.id("frequency_card"), new Action.Information(
            Component.literal("Frequency"),
            Component.literal("Something about this card being cool or smth like that")
    )));
    public static final RegistryObject<Action> ENCAPSULATE_CARD = registerAction("encapsulate_card", () -> new EncapsulateAction(Superposition.id("encapsulate_card"), new Action.Information(
            Component.literal("Encapsulate"),
            Component.literal("Something about this card being cool or smth like that")
    )));
    public static final RegistryObject<Action> MERGE_CARD = registerAction("merge_card", () -> new MergeAction(Superposition.id("merge_card"), new Action.Information(
            Component.literal("Merge"),
            Component.literal("Something about this card being cool or smth like that")
    )));
    public static final RegistryObject<Action> RETRIEVE_CARD = registerAction("retrieve_card", () -> new RetriveCard(Superposition.id("retrieve_card"), new Action.Information(
            Component.literal("Retrieve"),
            Component.literal("Something about this card being cool or smth like that")
    )));
    public static final RegistryObject<Action> SUBSTRING_CARD = registerAction("substring_card", () -> new SubstringCard(Superposition.id("substring_card"), new Action.Information(
            Component.literal("Substring"),
            Component.literal("Something about this card being cool or smth like that")
    )));
    public static final RegistryObject<Action> SLAVE_CARD = registerAction("slave_card", () -> new SlaveCard(Superposition.id("slave_card"), new Action.Information(
            Component.literal("Slave"),
            Component.literal("Something about this card being cool or smth like that")
    )),CompatabilityHandler.Mod.COMPUTERCRAFT);


    private static <T extends Action> RegistryObject<T> registerAction(String name, Supplier<T> action) {
        if (action.get().getThumbnailItem() == null) {
            SPRITESHEET.addSprite(action.get().getSelfReference());
        }
        return ACTION.register(name, action);
    }

    private static <T extends Action> RegistryObject<T> registerAction(String name, Supplier<T> action, CompatabilityHandler.Mod mod) {
        if (action.get().getThumbnailItem() == null) {
            SPRITESHEET.addSprite(action.get().getSelfReference());
        }
        return ACTION.register(name, action);
    }

    public static void bootstrap() {

    }

    /**
     * This method will return and or gather all registered actions.
     * The first time this action is called, it will collect all registered actions.
     * @return A list of registered actions
     */
    public static List<Action> getAllRegisteredActions() {
        if (actions.isEmpty()) {
            actions.addAll(SuperpositionActions.ACTION.asVanillaRegistry().stream().toList());
        }
        return actions;
    }

}
