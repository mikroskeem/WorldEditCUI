package com.mumfrey.worldeditcui.event.listeners;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mumfrey.worldeditcui.WorldEditCUI;
import com.mumfrey.worldeditcui.util.Vector3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

/**
 * Listener for WorldRenderEvent
 *
 * @author lahwran
 * @author yetanotherx
 * @author Adam Mummery-Smith
 */
public class CUIListenerWorldRender
{
	private final WorldEditCUI controller;

	private final MinecraftClient minecraft;
	private final CUIRenderContext ctx = new CUIRenderContext();

	public CUIListenerWorldRender(WorldEditCUI controller, MinecraftClient minecraft)
	{
		this.controller = controller;
		this.minecraft = minecraft;
	}

	public void onRender(final MatrixStack matrices, float partialTicks)
	{
		try
		{
			this.ctx.init(new Vector3(this.minecraft.gameRenderer.getCamera().getPos()), matrices, partialTicks);
			final var bufferBuilder = Tessellator.getInstance().getBuffer();
			/*bufferBuilder.setCameraPosition(
				 (float) this.ctx.cameraPos().getX(),
				 (float) this.ctx.cameraPos().getY(),
				 (float) this.ctx.cameraPos().getZ()
			);*/
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			// RenderSystem.blendFunc(GL32.GL_SRC_ALPHA, GL32.GL_ONE_MINUS_SRC_ALPHA);
			//RenderSystem.enableAlphaTest();
			// RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
			RenderSystem.disableTexture();
			RenderSystem.enableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.lineWidth(6.0f);
			final float oldFog = RenderSystem.getShaderFogStart();
			final Shader oldShader = RenderSystem.getShader();
			BackgroundRenderer.method_23792(); // disableFog
			RenderSystem.setShader(GameRenderer::getPositionColorShader);

			try
			{
				RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
				this.controller.renderSelections(this.ctx);
			}
			catch (Exception e) {
				this.controller.getDebugger().error("Error while attempting to render WorldEdit CUI", e);
			}

			bufferBuilder.unfixColor();
			RenderSystem.depthFunc(GL32.GL_LEQUAL);

			RenderSystem.setShaderFogStart(oldFog);
			RenderSystem.setShader(() -> oldShader);
			RenderSystem.depthMask(true);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
			// RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
		} catch (Exception ex)
		{
			this.controller.getDebugger().error("Failed while preparing state for WorldEdit CUI", ex);
		}
	}
}
