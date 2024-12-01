package org.modogthedev.superposition;

import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.modogthedev.superposition.client.renderer.CableRenderer;
import org.modogthedev.superposition.client.renderer.DebugRenderer;
import org.modogthedev.superposition.client.renderer.block.AmplifierBlockEntityRenderer;
import org.modogthedev.superposition.client.renderer.block.FilterBlockEntityRenderer;
import org.modogthedev.superposition.client.renderer.block.MonitorBlockEntityRenderer;
import org.modogthedev.superposition.client.renderer.block.SignalGeneratorBlockEntityRenderer;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.platform.BlockEntityRegistry;

public class SuperpositionClient {

    public static void init() {
        VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender((stage, levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc2, partialTicks, deltaTracker, camera, frustum) -> {
            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
                CableRenderer.renderCables(levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc2, partialTicks, deltaTracker, camera);
                CableRenderer.renderCableHeldPoint(levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc2, partialTicks, deltaTracker, camera);
                DebugRenderer.renderDebug(levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc2, partialTicks, deltaTracker, camera);
            }
        });
    }

    public static void registerBlockEntityRenderers(BlockEntityRegistry registry) {
        registry.registerBlockEntityRenderer(SuperpositionBlockEntities.SIGNAL_GENERATOR.get(), SignalGeneratorBlockEntityRenderer::new);
        registry.registerBlockEntityRenderer(SuperpositionBlockEntities.SIGNAL_READOUT.get(), MonitorBlockEntityRenderer::new);
        registry.registerBlockEntityRenderer(SuperpositionBlockEntities.AMPLIFIER.get(), AmplifierBlockEntityRenderer::new);
        registry.registerBlockEntityRenderer(SuperpositionBlockEntities.FILTER.get(), FilterBlockEntityRenderer::new);
    }

    public static void setScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }
}
