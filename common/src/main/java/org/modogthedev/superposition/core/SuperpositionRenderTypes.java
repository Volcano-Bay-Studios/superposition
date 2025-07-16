package org.modogthedev.superposition.core;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.Superposition;

import java.util.function.Function;

public class SuperpositionRenderTypes extends RenderType {

    private static final Function<ResourceLocation, RenderType> BLOOM_BLOCK_POLYGON_OFFSET = texture -> {
        RenderType.CompositeState blockPolygonOffsetBloom = RenderType.CompositeState.builder()
                .setOutputState(VeilRenderSystem.BLOOM_SHARD)
                .setShaderState(RenderStateShard.RENDERTYPE_CUTOUT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setWriteMaskState(RenderType.COLOR_WRITE)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(true);
        RenderType.CompositeState blockPolygonOffset = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_CUTOUT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setLayeringState(POLYGON_OFFSET_LAYERING)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return VeilRenderType.layered(create(Superposition.MODID + ":block_polygon_offset_bloom", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, TRANSIENT_BUFFER_SIZE, true, true, blockPolygonOffsetBloom), create(Superposition.MODID + ":block_polygon_offset_standard", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, TRANSIENT_BUFFER_SIZE, true, true, blockPolygonOffset));
    };

    private static final RenderType  BLOOM_POSITION_COLOR_POLYGON_OFFSET =
        VeilRenderType.layered(
        create(Superposition.MODID + ":block_polygon_offset_bloom", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, TRANSIENT_BUFFER_SIZE, true, true,
            RenderType.CompositeState.builder()
                .setOutputState(VeilRenderSystem.BLOOM_SHARD)
                .setShaderState(RenderStateShard.RENDERTYPE_CUTOUT_SHADER)
                .setWriteMaskState(RenderType.COLOR_WRITE)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(true)),
        create(Superposition.MODID + ":block_polygon_offset_standard", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, TRANSIENT_BUFFER_SIZE, true, true,
            RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_CUTOUT_SHADER)
                .setLayeringState(POLYGON_OFFSET_LAYERING)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true)));
    ;

    private static final Function<ResourceLocation, RenderType> BLOCK_POLYGON_OFFSET = texture -> {
        RenderType.CompositeState blockPolygonOffset = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_CUTOUT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setLayeringState(POLYGON_OFFSET_LAYERING)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return create(Superposition.MODID + ":block_polygon_offset", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, TRANSIENT_BUFFER_SIZE, true, true, blockPolygonOffset);
    };

    private static final RenderType POSITION_COLOR_POLYGON_OFFSET =
        create(Superposition.MODID + ":block_polygon_offset", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, TRANSIENT_BUFFER_SIZE, true, true,
           RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_CUTOUT_SHADER)
                .setLayeringState(POLYGON_OFFSET_LAYERING)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true));

//    private static final Function<ResourceLocation, RenderType> BLOCK_POLYGON_OFFSET = texture -> {
//
//        return ;
//    };

    private static final RenderType CABLE = create(
            Superposition.MODID + ":cable",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            TRANSIENT_BUFFER_SIZE,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(VeilRenderBridge.shaderState(Superposition.id("cable")))
                    .setTextureState(new RenderStateShard.TextureStateShard(Superposition.id("textures/misc/cable.png"), false, false))
                    .setLightmapState(LIGHTMAP)
                    .createCompositeState(false));

    public SuperpositionRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType bloomBlockPolygonOffset(ResourceLocation location) {
        return BLOOM_BLOCK_POLYGON_OFFSET.apply(location);
    }

    public static RenderType bloomPositionColorPolygonOffset() {
        return BLOOM_POSITION_COLOR_POLYGON_OFFSET;
    }

    public static RenderType blockPolygonOffset(ResourceLocation location) {
        return BLOCK_POLYGON_OFFSET.apply(location);
    }

    public static RenderType positionColorPolygonOffset() {
        return POSITION_COLOR_POLYGON_OFFSET;
    }


//    public static RenderType blockPolygonOffset(ResourceLocation location) {
//        return BLOCK_POLYGON_OFFSET.apply(location);
//    }

    public static RenderType cable() {
        return CABLE;
    }
}
