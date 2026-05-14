package org.modogthedev.superposition.core;

import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.widget.Widget;
import org.modogthedev.superposition.system.widget.widgets.ButtonWidget;
import org.modogthedev.superposition.system.widget.widgets.GaugeWidget;

import java.util.function.Supplier;

public class SuperpositionWidgets {
    public static final ResourceKey<Registry<Widget>> WIDGET_KEY = ResourceKey.createRegistryKey(Superposition.id("widget"));
    public static final RegistrationProvider<Widget> WIDGET = RegistrationProvider.get(WIDGET_KEY, Superposition.MODID);

    public static RegistryObject<GaugeWidget> GAUGE = register("gauge", GaugeWidget::new);
    public static RegistryObject<ButtonWidget> BUTTON = register("button", ButtonWidget::new);

    public static <T extends Widget> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return WIDGET.register(name, supplier);
    }

    public static void bootstrap() {
    }
}
