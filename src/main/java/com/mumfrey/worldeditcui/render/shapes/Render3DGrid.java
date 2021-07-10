package com.mumfrey.worldeditcui.render.shapes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mumfrey.worldeditcui.event.listeners.CUIRenderContext;
import com.mumfrey.worldeditcui.render.LineStyle;
import com.mumfrey.worldeditcui.render.RenderStyle;
import com.mumfrey.worldeditcui.util.BoundingBox;
import com.mumfrey.worldeditcui.util.Observable;
import com.mumfrey.worldeditcui.util.Vector3;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

/**
 * Draws the grid for a region between
 * two corners in a cuboid region.
 * 
 * @author yetanotherx
 * @author Adam Mummery-Smith
 */
public class Render3DGrid extends RenderRegion
{
	private static final float CULL_RANGE = 128.0F;
	public static final double MIN_SPACING = 1.0;
	
	private Vector3 first, second;
	private double spacing = 1.0;
	
	public Render3DGrid(RenderStyle style, BoundingBox region)
	{
		this(style, region.getMin(), region.getMax());
		if (region.isDynamic())
		{
			region.addObserver(this);
		}
	}
	
	public Render3DGrid(RenderStyle style, Vector3 first, Vector3 second)
	{
		super(style);
		this.first = first;
		this.second = second;
	}
	
	@Override
	public void notifyChanged(Observable<?> source)
	{
		this.setPosition((BoundingBox)source);
	}

	public void setPosition(BoundingBox region)
	{
		this.setPosition(region.getMin(), region.getMax());
	}
	
	public void setPosition(Vector3 first, Vector3 second)
	{
		this.first = first;
		this.second = second;
	}
	
	public Render3DGrid setSpacing(double spacing)
	{
		this.spacing = spacing;
		return this;
	}
	
	@Override
	public void render(CUIRenderContext ctx)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();
		final Vector3 camera = ctx.cameraPos();
		double x1 = this.first.getX() - camera.getX();
		double y1 = this.first.getY() - camera.getY();
		double z1 = this.first.getZ() - camera.getZ();
		double x2 = this.second.getX() - camera.getX();
		double y2 = this.second.getY() - camera.getY();
		double z2 = this.second.getZ() - camera.getZ();

		if (this.spacing != 1.0)
		{
			RenderSystem.disableCull();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);

			double[] vertices = {
					x1, y1, z1,  x2, y1, z1,  x2, y1, z2,  x1, y1, z2, // bottom
					x1, y2, z1,  x2, y2, z1,  x2, y2, z2,  x1, y2, z2, // top
					x1, y1, z1,  x1, y1, z2,  x1, y2, z2,  x1, y2, z1, // west
					x2, y1, z1,  x2, y2, z1,  x2, y2, z2,  x2, y1, z2, // east
					x1, y1, z1,  x1, y2, z1,  x2, y2, z1,  x2, y1, z1, // north
					x1, y1, z2,  x2, y1, z2,  x2, y2, z2,  x1, y2, z2  // south
			};

			for (LineStyle line : this.style.getLines())
			{
				if (line.prepare(this.style.getRenderType()))
				{
					buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
					line.applyColour(buf, 0.25F);
					for (int i = 0; i < vertices.length; i += 3)
					{
						buf.vertex(vertices[i], vertices[i + 1], vertices[i + 2]).next();
					}
					tessellator.draw();
				}
			}

			RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
			RenderSystem.enableCull();
		}
		
		if (this.spacing < Render3DGrid.MIN_SPACING)
		{
			return;
		}
		
		final double cullAt = Render3DGrid.CULL_RANGE * this.spacing;
		final double cullAtY = cullAt - MathHelper.fractionalPart(y1);
		final double cullAtX = cullAt - MathHelper.fractionalPart(x1);
		final double cullAtZ = cullAt - MathHelper.fractionalPart(z1);
		for (LineStyle line : this.style.getLines())
		{
			if (!line.prepare(this.style.getRenderType()))
			{
				continue;
			}
			
			buf.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
			line.applyColour(buf);

			for (double y = Math.max(y1, -cullAtY) + OFFSET; y <= y2 + OFFSET && y <= cullAtY; y += this.spacing)
			{
				buf.vertex(x1, y, z2).next();
				buf.vertex(x2, y, z2).next();
				buf.vertex(x1, y, z1).next();
				buf.vertex(x2, y, z1).next();
				buf.vertex(x1, y, z1).next();
				buf.vertex(x1, y, z2).next();
				buf.vertex(x2, y, z1).next();
				buf.vertex(x2, y, z2).next();
			}
			
			for (double x = Math.max(x1, -cullAtX); x <= x2 && x <= cullAtX; x += this.spacing)
			{
				buf.vertex(x, y1, z1).next();
				buf.vertex(x, y2, z1).next();
				buf.vertex(x, y1, z2).next();
				buf.vertex(x, y2, z2).next();
				buf.vertex(x, y2, z1).next();
				buf.vertex(x, y2, z2).next();
				buf.vertex(x, y1, z1).next();
				buf.vertex(x, y1, z2).next();
			}
			
			for (double z = Math.max(z1, -cullAtZ); z <= z2 && z <= cullAtZ; z += this.spacing)
			{
				buf.vertex(x1, y1, z).next();
				buf.vertex(x2, y1, z).next();
				buf.vertex(x1, y2, z).next();
				buf.vertex(x2, y2, z).next();
				buf.vertex(x2, y1, z).next();
				buf.vertex(x2, y2, z).next();
				buf.vertex(x1, y1, z).next();
				buf.vertex(x1, y2, z).next();
			}
			
			tessellator.draw();
		}
	}
}
