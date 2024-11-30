package org.modogthedev.superposition;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.modogthedev.superposition.client.renderer.CableRenderer;
import org.modogthedev.superposition.client.renderer.DebugRenderer;
import org.modogthedev.superposition.client.renderer.block.AmplifierBlockEntityRenderer;
import org.modogthedev.superposition.client.renderer.block.FilterBlockEntityRenderer;
import org.modogthedev.superposition.client.renderer.block.SignalGeneratorBlockEntityRenderer;
import org.modogthedev.superposition.client.renderer.block.MonitorBlockEntityRenderer;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionMessages;
import org.modogthedev.superposition.platform.PlatformHelper;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.CarabinerManager;
import org.modogthedev.superposition.system.signal.ClientSignalManager;

public class SuperpositionClient {
    public static void init() {
        PlatformHelper.register();
        SuperpositionMessages.registerClient();

        ClientTickEvent.CLIENT_LEVEL_POST.register(ClientSignalManager::tick);
        ClientTickEvent.CLIENT_LEVEL_POST.register(CableManager::clientTick);
        ClientTickEvent.CLIENT_LEVEL_POST.register(CarabinerManager::clientTick);
        VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender((stage, levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc2, partialTicks, deltaTracker, camera, frustum) -> {
            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
                CableRenderer.renderCables(levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc2, partialTicks, deltaTracker, camera);
                CableRenderer.renderCableHeldPoint(levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc2, partialTicks, deltaTracker, camera);
                DebugRenderer.renderDebug(levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc2, partialTicks, deltaTracker, camera);
            }
        });
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(SuperpositionBlockEntities.SIGNAL_GENERATOR.get(), SignalGeneratorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SuperpositionBlockEntities.SIGNAL_READOUT.get(), MonitorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SuperpositionBlockEntities.AMPLIFIER.get(), AmplifierBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SuperpositionBlockEntities.FILTER.get(), FilterBlockEntityRenderer::new);
    }

    public static void setScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }
}
