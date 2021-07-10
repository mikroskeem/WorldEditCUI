package com.mumfrey.worldeditcui.event.listeners;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mumfrey.worldeditcui.util.Vector3;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Consumer;

/**
 * State related to CUI rendering.
 */
public final class CUIRenderContext {
    private Vector3 cameraPos;
    private MatrixStack matrices = RenderSystem.getModelViewStack();
    private float dt;

    public Vector3 cameraPos() {
        return this.cameraPos;
    }

    public MatrixStack matrices() {
        return RenderSystem.getModelViewStack();
    }

    public void applyMatrices() {
        RenderSystem.applyModelViewMatrix();
    }

    public float dt() {
        return this.dt;
    }

    public void withCameraAt(final Vector3 pos, final Consumer<CUIRenderContext> action) {
        final Vector3 oldPos = this.cameraPos;
        this.cameraPos = pos;
        try {
            action.accept(this);
        } finally {
            this.cameraPos = oldPos;
        }
    }

    void init(final Vector3 cameraPos, final MatrixStack matrices, final float dt) {
        this.cameraPos = cameraPos;
        this.matrices = matrices;
        this.dt = dt;
    }

    /**
     * Empty state. To be called at the end of a frame.
     */
    void reset() {
        this.cameraPos = null;
        this.matrices = null;
    }
}
