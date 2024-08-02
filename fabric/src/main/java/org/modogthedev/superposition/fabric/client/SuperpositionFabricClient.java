package org.modogthedev.superposition.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import org.modogthedev.superposition.SuperpositionClient;
import org.modogthedev.superposition.core.SuperpositionBlocks;

public class SuperpositionFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SuperpositionClient.init();
        SuperpositionClient.registerBlockEntityRenderers();
        registerBlockRenderLayers();
    }

    private void registerBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(SuperpositionBlocks.SIGNAL_READOUT.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(SuperpositionBlocks.ANTENNA.get(), RenderType.cutout());
    }
}
