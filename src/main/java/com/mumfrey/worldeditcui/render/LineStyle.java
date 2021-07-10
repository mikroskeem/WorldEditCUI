package com.mumfrey.worldeditcui.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mumfrey.worldeditcui.render.RenderStyle.RenderType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.FixedColorVertexConsumer;

/**
 * Stores data about a line that can be rendered
 * 
 * @author lahwran
 * @author yetanotherx
 * @author Adam Mummery-Smith
 */
public class LineStyle
{
	public static final float DEFAULT_WIDTH = 3.0f;

	public final float lineWidth;
	public final int red, green, blue, alpha;
	public final RenderType renderType;
	
	public LineStyle(RenderType renderType, float lineWidth, int red, int green, int blue)
	{
		this(renderType, lineWidth, red, green, blue, 0xff);
	}
	
	public LineStyle(RenderType renderType, float lineWidth, int red, int green, int blue, int alpha)
	{
		this.lineWidth = lineWidth;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.renderType = renderType;
	}
	
	/**
	 * Sets the lineWidth and depthFunction based on this style
	 */
	public boolean prepare(RenderType renderType)
	{
		if (this.renderType.matches(renderType))
		{
			RenderSystem.lineWidth(this.lineWidth);
			RenderSystem.depthFunc(this.renderType.depthFunc);
			return true;
		}
		
		return false;
	}

	public void applyColour(final FixedColorVertexConsumer consumer)
	{
		consumer.fixedColor(this.red, this.green, this.blue, this.alpha);
	}

	public void applyColour(final FixedColorVertexConsumer consumer, final float tint)
	{
		consumer.fixedColor(this.red, this.green, this.blue, Math.round(this.alpha * tint));
	}
}
