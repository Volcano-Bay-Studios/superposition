package org.modogthedev.superposition;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.modogthedev.superposition.client.renderer.block.AmplifierBlockEntityRenderer;
import org.modogthedev.superposition.client.renderer.block.SignalGeneratorBlockEntityRenderer;
import org.modogthedev.superposition.client.renderer.block.SignalReadoutBlockEntityRenderer;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.platform.PlatformHelper;
import org.modogthedev.superposition.system.signal.ClientSignalManager;

public class SuperpositionClient {
    public static void init() {
        PlatformHelper.register();
        registerBlockEntityRenderers();

        ClientTickEvent.CLIENT_LEVEL_POST.register(ClientSignalManager::tick);
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(SuperpositionBlockEntities.SIGNAL_GENERATOR.value(), SignalGeneratorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SuperpositionBlockEntities.SIGNAL_READOUT.value(), SignalReadoutBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SuperpositionBlockEntities.AMPLIFIER.value(), AmplifierBlockEntityRenderer::new);
    }

    public static void setScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }
}
