package com.jamieswhiteshirt.clothesline.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.light.LightingProvider;
import org.lwjgl.opengl.GL11;

public class ClotheslineRenderLayers extends RenderLayer {
    public ClotheslineRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    private static final VertexFormat CLOTHESLINE_VERTEX_FORMAT = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder()
        .put("Position", VertexFormats.POSITION_ELEMENT)
        .put("Normal", VertexFormats.NORMAL_ELEMENT)
        .put("UV0", VertexFormats.TEXTURE_0_ELEMENT)
        .put("UV2", VertexFormats.LIGHT_ELEMENT)
        .build()
    );
    */

    private static final Identifier CLOTHESLINE_TEXTURE = new Identifier("clothesline", "textures/misc/clothesline.png");
    // TODO: What is a reasonable default buffer size?
    private static final RenderLayer CLOTHESLINE = RenderLayer.of("clothesline", CLOTHESLINE_VERTEX_FORMAT, VertexFormat.DrawMode.QUADS, 256, RenderLayer.MultiPhaseParameters.builder()
        .texture(new RenderPhase.Texture(CLOTHESLINE_TEXTURE, false, false))
        .shader(new Shader(ClotheslineShader::getInstance))
        .transparency(NO_TRANSPARENCY)
        .lightmap(ENABLE_LIGHTMAP)
        .build(true)
    );

    public static RenderLayer getClothesline() {
        return CLOTHESLINE;
    }
}
