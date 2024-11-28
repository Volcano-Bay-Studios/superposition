package org.modogthedev.superposition.core;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.api.client.render.VeilRenderBridge;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.Superposition;

import java.util.function.Function;

public class SuperpositionRenderTypes extends RenderType {

    private static final Function<ResourceLocation, RenderType> BLOCK_POLYGON_OFFSET = Util.memoize(texture -> {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_TRANSLUCENT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLayeringState(POLYGON_OFFSET_LAYERING).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create(Superposition.MODID + ":block_polygon_offset", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, TRANSIENT_BUFFER_SIZE, true, true, rendertype$compositestate);
    });
    private static final RenderType CABLE = create(
            Superposition.MODID + ":cable",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            TRANSIENT_BUFFER_SIZE,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(VeilRenderBridge.shaderState(Superposition.id("cable")))
                    .setTextureState(new RenderStateShard.TextureStateShard(Superposition.id("textures/screen/cable.png"), false, false))
                    .setLightmapState(LIGHTMAP)
                    .createCompositeState(false));

    public SuperpositionRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType blockPolygonOffset(ResourceLocation location) {
        return BLOCK_POLYGON_OFFSET.apply(location);
    }

    public static RenderType cable() {
        return CABLE;
    }
}
