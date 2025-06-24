package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.compat.CompatabilityHandler;
import org.modogthedev.superposition.screens.utils.ActionSpritesheet;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.actions.*;
import org.modogthedev.superposition.system.cards.actions.configuration.ActionConfiguration;
import org.modogthedev.superposition.system.cards.actions.configuration.DirectionConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SuperpositionActions {

    private static final List<Action> actions = new ArrayList<>();

    public static final ActionSpritesheet SPRITESHEET = new ActionSpritesheet(Superposition.id("textures/screen/action_spritesheet.png"), 256);

    public static final ResourceKey<Registry<ActionConfiguration>> ACTION_CONFIGURATION_KEY = ResourceKey.createRegistryKey(Superposition.id("action_configurations"));
    public static final RegistrationProvider<ActionConfiguration> ACTION_CONFIGURATIONS = RegistrationProvider.get(ACTION_CONFIGURATION_KEY, Superposition.MODID);

    public static final RegistryObject<ActionConfiguration> DIRECTION = ACTION_CONFIGURATIONS.register("direction", () -> (new DirectionConfiguration(Component.literal("Direction"))));


    public static final ResourceKey<Registry<Action>> ACTION_KEY = ResourceKey.createRegistryKey(Superposition.id("action"));
    public static final RegistrationProvider<Action> ACTION = RegistrationProvider.get(ACTION_KEY, Superposition.MODID);

    public static final RegistryObject<Action> COLOR = registerAction("color", () -> new ColorAction(Superposition.id("color"), new Action.Information(
            Component.literal("Color"),
            Component.literal("Retrieves the color of the block that is being analysed"),
            Action.Type.PERIPHERAL
    )));
    public static final RegistryObject<Action> SIGN = registerAction("sign", () -> new SignCard(Superposition.id("sign"), new Action.Information(
            Component.literal("Sign"),
            Component.literal("Retrieves the text of the block that is being analysed"),
            Action.Type.PERIPHERAL
    )));
    public static final RegistryObject<Action> REDSTONE = registerAction("redstone", () -> new RedstoneCard(Superposition.id("redstone"), new Action.Information(
            Component.literal("Redstone"),
            Component.literal("Retrieves the redstone value of the block that is being analysed, or sets the redstone value to the input in a manipulator"),
            Action.Type.PERIPHERAL
    )));
    public static final RegistryObject<Action> CONTAINER = registerAction("container", () -> new ContainerAction(Superposition.id("container"), new Action.Information(
            Component.literal("Container"),
            Component.literal("Retrieves the contents of the block that is being analysed"),
            Action.Type.PERIPHERAL
    )));
    public static final RegistryObject<Action> IDENTITY = registerAction("identity", () -> new IdentityCard(Superposition.id("identity"), new Action.Information(
            Component.literal("Identity"),
            Component.literal("Retrieves the name of the block that is being analysed"),
            Action.Type.PERIPHERAL
    )));
    public static final RegistryObject<Action> TEMPERATURE = registerAction("temperature", () -> new TemperatureCard(Superposition.id("temperature"), new Action.Information(
            Component.literal("Temperature"),
            Component.literal("Retrieves the temperature of the block that is being analysed"),
            Action.Type.PERIPHERAL
    )));
    public static final RegistryObject<Action> DISTANCE = registerAction("distance", () -> new DistanceAction(Superposition.id("distance"), new Action.Information(
            Component.literal("Distance"),
            Component.literal("Retrieves the distance to the block that is being analysed"),
            Action.Type.PERIPHERAL
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
            Component.literal("Something about this card being cool or smth like that"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> MERGE = registerAction("merge", () -> new MergeAction(Superposition.id("merge"), new Action.Information(
            Component.literal("Merge"),
            Component.literal("Something about this card being cool or smth like that"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> RETRIEVE = registerAction("retrieve", () -> new RetriveCard(Superposition.id("retrieve"), new Action.Information(
            Component.literal("Retrieve"),
            Component.literal("Something about this card being cool or smth like that"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> SUBSTRING = registerAction("substring", () -> new SubstringCard(Superposition.id("substring"), new Action.Information(
            Component.literal("Substring"),
            Component.literal("Something about this card being cool or smth like that"),
            Action.Type.MODIFY
    )));
    public static final RegistryObject<Action> OUTPUT = registerAction("output", () -> new OutputAction(Superposition.id("output"), new Action.Information(
            Component.literal("Output"),
            Component.literal("Pushes the provided signal to the buffer of the selected face"),
            Action.Type.OUTPUT
    )));
    public static final RegistryObject<Action> INPUT = registerAction("input", () -> new InputAction(Superposition.id("input"), new Action.Information(
            Component.literal("Input"),
            Component.literal("Retrieves the signal from the specified input buffer"),
            Action.Type.OUTPUT
    )));
    public static final RegistryObject<Action> SLAVE = registerAction("slave", () -> new SlaveCard(Superposition.id("slave"), new Action.Information(
            Component.literal("Slave"),
            Component.literal("Something about this card being cool or smth like that"),
            Action.Type.OTHER
    )), CompatabilityHandler.Mod.COMPUTERCRAFT);

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

}
