package org.modogthedev.superposition.core;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.widget.WidgetRenderer;
import org.modogthedev.superposition.system.widget.renderers.ButtonWidgetRenderer;
import org.modogthedev.superposition.system.widget.renderers.GaugeWidgetRenderer;
import org.modogthedev.superposition.system.widget.widgets.ButtonWidget;
import org.modogthedev.superposition.system.widget.widgets.GaugeWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class SuperpositionWidgetRenderers {
    public static final ResourceKey<Registry<WidgetRenderer<?>>> WIDGET_RENDERER_KEY = ResourceKey.createRegistryKey(Superposition.id("widget_renderer"));
    public static final RegistrationProvider<WidgetRenderer<?>> WIDGET_RENDERER = RegistrationProvider.get(WIDGET_RENDERER_KEY, Superposition.MODID);

    public static RegistryObject<WidgetRenderer<GaugeWidget>> GAUGE = register("gauge",(modelMap -> () -> new GaugeWidgetRenderer(modelMap)),"gauge_needle");
    public static RegistryObject<WidgetRenderer<ButtonWidget>> BUTTON = register("button",(modelMap -> () -> new ButtonWidgetRenderer(modelMap)),"button_button");

    public static <T extends WidgetRenderer<?>> RegistryObject<T> register(String name, Function<Map<String, PartialModel>, Supplier<T>> supplier, String ... paths) {
        List<String> finalPaths = new ArrayList<>();
        finalPaths.add(name);
        finalPaths.addAll(List.of(paths));
        HashMap<String, PartialModel> modelMap = new HashMap<>();
        for (String finalPath : finalPaths) {
            PartialModel widget = SuperpositionPartials.widget(finalPath);
            modelMap.put(finalPath,widget);
        }

        return WIDGET_RENDERER.register(name, supplier.apply(modelMap));
    }

    public static void bootstrap() {
    }
}
