package com.mumfrey.worldeditcui.render.shapes;

import com.mumfrey.worldeditcui.event.listeners.CUIRenderContext;
import com.mumfrey.worldeditcui.render.LineStyle;
import com.mumfrey.worldeditcui.render.RenderStyle;
import com.mumfrey.worldeditcui.render.points.PointRectangle;
import com.mumfrey.worldeditcui.util.Vector2;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import java.util.List;

/**
 * Draws the grid for a polygon region
 * 
 * @author yetanotherx
 * @author lahwran
 * @author Adam Mummery-Smith
 */
public class Render2DGrid extends RenderRegion
{
	private final List<PointRectangle> points;
	private final int min, max;
	
	public Render2DGrid(RenderStyle style, List<PointRectangle> points, int min, int max)
	{
		super(style);
		this.points = points;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public void render(CUIRenderContext ctx)
	{
		double off = 0.03;
		for (double height = this.min; height <= this.max + 1; height++)
		{
			this.drawPoly(ctx, height + off);
		}
	}
	
	protected void drawPoly(final CUIRenderContext ctx, double height)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();
		for (LineStyle line : this.style.getLines())
		{
			if (!line.prepare(this.style.getRenderType()))
			{
				continue;
			}
			
			buf.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
			line.applyColour(buf);
			for (PointRectangle point : this.points)
			{
				if (point != null)
				{
					Vector2 pos = point.getPoint();
					double x = pos.getX() - ctx.cameraPos().getX();
					double z = pos.getY() - ctx.cameraPos().getZ();
					buf.vertex(x + 0.5, height - ctx.cameraPos().getY(), z + 0.5).next();
				}
			}

			// Repeat of initial vertex for LINE_STRIP
			final PointRectangle initialRepeat = this.points.get(0);
			if (initialRepeat != null)
			{
				Vector2 pos = initialRepeat.getPoint();
				double x = pos.getX() - ctx.cameraPos().getX();
				double z = pos.getY() - ctx.cameraPos().getZ();
				buf.vertex(x + 0.5, height - ctx.cameraPos().getY(), z + 0.5).next();
			}
			tessellator.draw();
		}
	}
}
