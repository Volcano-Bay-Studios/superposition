package org.modogthedev.superposition;

import com.mojang.blaze3d.audio.Channel;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundEngine;
import org.modogthedev.superposition.client.renderer.CableRenderer;
import org.modogthedev.superposition.client.renderer.DebugRenderer;
import org.modogthedev.superposition.client.renderer.block.*;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionPartials;
import org.modogthedev.superposition.core.SuperpositionWidgetRenderers;
import org.modogthedev.superposition.platform.BlockEntityRegistry;
import org.modogthedev.superposition.system.sound.ClientAudioManager;

public class SuperpositionClient {

    public static void init() {
//        PonderIndex.addPlugin(new SuperpositionPonderPlugin());
        SuperByteBufferCache.getInstance().registerCompartment(CachedBuffers.PARTIAL);

        SuperpositionPartials.bootstrap();
        SuperpositionWidgetRenderers.bootstrap();
        ClientAudioManager.setup();

        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, partialTicks, deltaTracker, camera, frustum) -> {
            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
                CableRenderer.renderCables(projectionMatrix, frustumMatrix, deltaTracker, camera);
                CableRenderer.renderOverlays(levelRenderer, bufferSource, matrixStack, projectionMatrix, frustumMatrix, partialTicks, deltaTracker, camera);
                DebugRenderer.renderDebug(levelRenderer, bufferSource, matrixStack, projectionMatrix, frustumMatrix, partialTicks, deltaTracker, camera);
                CableRenderer.renderCableHeldPoint(levelRenderer, bufferSource, matrixStack, projectionMatrix, frustumMatrix, partialTicks, deltaTracker, camera);
            }
        });
    }

    public static void registerBlockEntityRenderers(BlockEntityRegistry registry) {
        registry.registerBlockEntityRenderer(SuperpositionBlockEntities.SIGNAL_GENERATOR.get(), SignalGeneratorBlockEntityRenderer::new);
        registry.registerBlockEntityRenderer(SuperpositionBlockEntities.MONITOR.get(), MonitorBlockEntityRenderer::new);
        registry.registerBlockEntityRenderer(SuperpositionBlockEntities.AMPLIFIER.get(), AmplifierBlockEntityRenderer::new);
        registry.registerBlockEntityRenderer(SuperpositionBlockEntities.FILTER.get(), FilterBlockEntityRenderer::new);
        registry.registerBlockEntityRenderer(SuperpositionBlockEntities.COMBINATOR.get(), CombinatorBlockEntityRenderer::new);
        registry.registerBlockEntityRenderer(SuperpositionBlockEntities.CONSTANT_COMBINATOR.get(), ConstantCombinatorBlockEntityRenderer::new);
        registry.registerBlockEntityRenderer(SuperpositionBlockEntities.SPOTLIGHT.get(), SpotlightBlockEntityRenderer::new);
        registry.registerBlockEntityRenderer(SuperpositionBlockEntities.PANEL.get(), PanelBlockEntityRenderer::new);
    }

    public static void setScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }

    public static void playStreaming(SoundEngine soundEngine, Channel channel, AudioStream stream) {
        ClientAudioManager.playStreaming(soundEngine, channel, stream);
    }
}
