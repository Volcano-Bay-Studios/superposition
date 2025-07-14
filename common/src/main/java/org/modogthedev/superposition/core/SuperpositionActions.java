package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.blockentity.CombinatorBlockEntity;
import org.modogthedev.superposition.compat.CompatabilityHandler;
import org.modogthedev.superposition.screens.utils.ActionSpritesheet;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.actions.*;
import org.modogthedev.superposition.system.card.actions.configuration.ActionConfiguration;
import org.modogthedev.superposition.system.card.actions.configuration.DirectionConfiguration;
import org.modogthedev.superposition.system.card.actions.configuration.EnumConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SuperpositionActions {

    private static final List<Action> actions = new ArrayList<>();
    private static final List<ActionConfiguration> actionConfigurations = new ArrayList<>();

    public static final ActionSpritesheet SPRITESHEET = new ActionSpritesheet(Superposition.id("textures/screen/action_spritesheet.png"), 256);

    public static final ResourceKey<Registry<ActionConfiguration>> ACTION_CONFIGURATION_KEY = ResourceKey.createRegistryKey(Superposition.id("action_configurations"));
    public static final RegistrationProvider<ActionConfiguration> ACTION_CONFIGURATIONS = RegistrationProvider.get(ACTION_CONFIGURATION_KEY, Superposition.MODID);

    public static final RegistryObject<ActionConfiguration> DIRECTION_CONFIGURATION = ACTION_CONFIGURATIONS.register("direction", () -> new DirectionConfiguration(Component.literal("Direction")));
    public static final RegistryObject<ActionConfiguration> COMBINATOR_CONFIGURATION = ACTION_CONFIGURATIONS.register("combinator", () -> new EnumConfiguration(Component.literal("Combinator"), CombinatorBlockEntity.Modes.values()));


    public static final ResourceKey<Registry<Action>> ACTION_KEY = ResourceKey.createRegistryKey(Superposition.id("action"));
    public static final RegistrationProvider<Action> ACTION = RegistrationProvider.get(ACTION_KEY, Superposition.MODID);

    public static final RegistryObject<Action> INPUT = registerAction("input", () -> new InputAction(Superposition.id("input"), new Action.Information(
            Component.literal("Input"),
            Component.literal("Retrieves the signal from the specified buffer"),
            Action.Type.OUTPUT
    )));
    public static final RegistryObject<Action> OUTPUT = registerAction("output", () -> new OutputAction(Superposition.id("output"), new Action.Information(
            Component.literal("Output"),
            Component.literal("Pushes the provided signal to the specified buffer"),
            Action.Type.OUTPUT
    )));
    public static final RegistryObject<Action> ENCODE = registerAction("encode", () -> new EncodeAction(Superposition.id("encode"), new Action.Information(
            Component.literal("Encode"),
            Component.literal("Replaces the first signals encoded data with the seconds"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> GROUP = registerAction("group", () -> new GroupAction(Superposition.id("group"), new Action.Information(
            Component.literal("Group"),
            Component.literal("Groups multiple signal lists into one"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> SPLIT = registerAction("split", () -> new SplitAction(Superposition.id("split"), new Action.Information(
            Component.literal("Split"),
            Component.literal("Duplicates a signal list into two copies"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> OPTIONAL = registerAction("optional", () -> new OptionalAction(Superposition.id("optional"), new Action.Information(
            Component.literal("Optional"),
            Component.literal("Returns the first signal if, and only if, the second signals boolean value is true"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> AMPLITUDE = registerAction("amplitude", () -> new AmplitudeAction(Superposition.id("amplitude"), new Action.Information(
            Component.literal("Amplitude"),
            Component.literal("Retrieves the amplitude of the signal"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> FREQUENCY = registerAction("frequency", () -> new FrequencyCard(Superposition.id("frequency"), new Action.Information(
            Component.literal("Frequency"),
            Component.literal("Retrieves the frequency of the signal"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> ENCAPSULATE = registerAction("encapsulate", () -> new EncapsulateAction(Superposition.id("encapsulate"), new Action.Information(
            Component.literal("Encapsulate"),
            Component.literal("Captures the first signal as a tag with a key of the second signal"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> MERGE = registerAction("merge", () -> new MergeAction(Superposition.id("merge"), new Action.Information(
            Component.literal("Merge"),
            Component.literal("Merges tags together"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> RETRIEVE = registerAction("retrieve", () -> new RetriveCard(Superposition.id("retrieve"), new Action.Information(
            Component.literal("Retrieve"),
            Component.literal("Retrieve the contents of a tag using the second signal as a tag"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> REPROGRAM = registerAction("reprogram", () -> new ReprogramAction(Superposition.id("reprogram"), new Action.Information(
            Component.literal("Reprogram"),
            Component.literal("Updates the program inside the computer with the tag in the signal"),
            Action.Type.OUTPUT
    )));
    public static final RegistryObject<Action> PROGRAM = registerAction("program", () -> new ProgramAction(Superposition.id("program"), new Action.Information(
            Component.literal("Program"),
            Component.literal("Returns the program stored inside the computer"),
            Action.Type.OUTPUT
    )));
    public static final RegistryObject<Action> SUBSTRING = registerAction("substring", () -> new SubstringCard(Superposition.id("substring"), new Action.Information(
            Component.literal("Substring"),
            Component.literal("Cuts a string at the the number encoded the second signal, if the second signal is negative it will cut from the front instead"),
            Action.Type.MODIFY
    )));

    private static <T extends Action> RegistryObject<T> registerAction(String name, Supplier<T> action) {
        if (action.get().getThumbnailItem() == null) {
            SPRITESHEET.addSprite(Superposition.id(name));
        }
        return ACTION.register(name, action);
    }

    private static <T extends Action> RegistryObject<T> registerAction(String name, Supplier<T> action, CompatabilityHandler.Mod mod) {
        if (action.get().getThumbnailItem() == null) {
            SPRITESHEET.addSprite(Superposition.id(name));
        }
        return ACTION.register(name, action);
    }


    public static void bootstrap() {
        for (ActionConfiguration action : getAllRegisteredActionConfigurations()) {
            action.getSelfReference();
        }
        for (Action action : getAllRegisteredActions()) {
            action.getSelfReference();
        }
    }

    /**
     * This method will return and or gather all registered actions.
     * The first time this action is called, it will collect all registered actions.
     *
     * @return A list of registered actions
     */
    public static List<Action> getAllRegisteredActions() {
        if (actions.isEmpty()) {
            actions.addAll(SuperpositionActions.ACTION.asVanillaRegistry().stream().toList());
        }
        return actions;
    }

    public static List<ActionConfiguration> getAllRegisteredActionConfigurations() {
        if (actionConfigurations.isEmpty()) {
            actionConfigurations.addAll(SuperpositionActions.ACTION_CONFIGURATIONS.asVanillaRegistry().stream().toList());
        }
        return actionConfigurations;
    }

}
