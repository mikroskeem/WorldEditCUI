package eu.mikroskeem.worldeditcui.mixins;

import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.ByteBuffer;

@Mixin(BufferRenderer.class)
public class BufferRendererMixin {

    @Redirect(method = "draw(Ljava/nio/ByteBuffer;Lnet/minecraft/client/render/VertexFormat$DrawMode;Lnet/minecraft/client/render/VertexFormat;ILnet/minecraft/client/render/VertexFormat$IntType;IZ)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/VertexFormat$DrawMode;LINES:Lnet/minecraft/client/render/VertexFormat$DrawMode;", ordinal = 0, opcode = Opcodes.GETSTATIC))
    private static VertexFormat.DrawMode wecui$setWidthOnDebugLines(
        final ByteBuffer data,
        final VertexFormat.DrawMode mode
    ) {
        if (mode == VertexFormat.DrawMode.DEBUG_LINE_STRIP || mode == VertexFormat.DrawMode.DEBUG_LINES) {
            return mode;
        } else {
            return VertexFormat.DrawMode.LINES;
        }
    }
}
