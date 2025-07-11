package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.behavior.Behavior;
import org.modogthedev.superposition.system.behavior.behaviors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SuperpositionBehaviors {
    public static final ResourceKey<Registry<Behavior>> BEHAVIOR_KEY = ResourceKey.createRegistryKey(Superposition.id("behavior"));
    public static final RegistrationProvider<Behavior> BEHAVIOR = RegistrationProvider.get(BEHAVIOR_KEY, Superposition.MODID);
    public static final List<Behavior> behaviors = new ArrayList<>();

    public static final RegistryObject<Behavior> COLOR = registerBehavior("color", (resourceLocation -> () -> new ColorBehavior(resourceLocation)));
    public static final RegistryObject<Behavior> CONTAINER = registerBehavior("container", (resourceLocation -> () -> new ContainerBehavior(resourceLocation)));
    public static final RegistryObject<Behavior> IDENTITY = registerBehavior("identity", (resourceLocation -> () -> new IdentityBehavior(resourceLocation)));
    public static final RegistryObject<Behavior> DISTANCE = registerBehavior("distance", (resourceLocation -> () -> new DistanceBehavior(resourceLocation)));
    public static final RegistryObject<Behavior> REDSTONE = registerBehavior("redstone", (resourceLocation -> () -> new RedstoneBehavior(resourceLocation)));
    public static final RegistryObject<Behavior> SIGN = registerBehavior("sign", (resourceLocation -> () -> new SignBehavior(resourceLocation)));

    public static RegistryObject<Behavior> registerBehavior(String name, Function<ResourceLocation, Supplier<Behavior>> resourceLocationFunction) {
        return BEHAVIOR.register(name,resourceLocationFunction.apply(Superposition.id(name)));
    }

    public static void bootstrap(){
        behaviors.addAll(BEHAVIOR.asVanillaRegistry().stream().toList());
    }
}
