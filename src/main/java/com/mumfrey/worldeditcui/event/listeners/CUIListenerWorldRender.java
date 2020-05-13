package com.mumfrey.worldeditcui.event.listeners;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mumfrey.worldeditcui.WorldEditCUI;
import com.mumfrey.worldeditcui.util.Vector3;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL11;

/**
 * Listener for WorldRenderEvent
 * 
 * @author lahwran
 * @author yetanotherx
 * @author Adam Mummery-Smith
 */
public class CUIListenerWorldRender
{
	private WorldEditCUI controller;
	
	private MinecraftClient minecraft;
	
	public CUIListenerWorldRender(WorldEditCUI controller, MinecraftClient minecraft)
	{
		this.controller = controller;
		this.minecraft = minecraft;
	}
	
	public void onRender(float partialTicks)
	{
		try
		{
			RenderSystem.glMultiTexCoord2f(33985, 240.0F, 240.0F);
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderSystem.enableBlend();
			RenderSystem.enableAlphaTest();
			RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
			RenderSystem.disableTexture();
			RenderSystem.enableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.pushMatrix();
			RenderSystem.disableFog();
			
			try
			{
				Vector3 cameraPos = new Vector3(this.minecraft.gameRenderer.getCamera().getPos());
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
				this.controller.renderSelections(cameraPos, partialTicks);
			}
			catch (Exception e)
			{
			}

			RenderSystem.depthFunc(GL11.GL_LEQUAL);
			RenderSystem.popMatrix();

			RenderSystem.depthMask(true);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
			RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
		}
		catch (Exception ex) {}
	}
}
