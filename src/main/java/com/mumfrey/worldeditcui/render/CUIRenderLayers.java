package com.mumfrey.worldeditcui.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import java.util.OptionalDouble;

public abstract class CUIRenderLayers extends RenderLayer {

    public CUIRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    /*public static final RenderLayer.MultiPhase OUTLINE = RenderLayer.of(
        "worldeditcui:outline",
        VertexFormats.LINES,
        VertexFormat.DrawMode.LINE_STRIP,
        256,
        MultiPhaseParameters.builder()
            .shader(LINES_SHADER)
            .lineWidth(new LineWidth(OptionalDouble.of(3.0f)))
            .layering(VIEW_OFFSET_Z_LAYERING)
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .texture(NO_TEXTURE)
            .target(TRANSLUCENT_TARGET)
            .writeMaskState(ALL_MASK)
            .cull(DISABLE_CULLING)
            .build(false)
    );

    public static final RenderLayer.MultiPhase OVERLAY = RenderLayer.of(
            "worldeditcui:overlay",
            VertexFormats.LINES,
            VertexFormat.DrawMode.LINE_STRIP,
            256,
            MultiPhaseParameters.builder()
                    .shader(LINES_SHADER)
                    .lineWidth(new LineWidth(OptionalDouble.of(3.0f)))
                    .layering(VIEW_OFFSET_Z_LAYERING)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .texture(NO_TEXTURE)
                    .target(TRANSLUCENT_TARGET)
                    .writeMaskState(ALL_MASK)
                    .cull(DISABLE_CULLING)
                    .build(false)
    );*/
}
