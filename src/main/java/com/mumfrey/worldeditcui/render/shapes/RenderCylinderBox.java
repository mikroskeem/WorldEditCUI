package com.mumfrey.worldeditcui.render.shapes;

import com.mumfrey.worldeditcui.event.listeners.CUIRenderContext;
import com.mumfrey.worldeditcui.render.LineStyle;
import com.mumfrey.worldeditcui.render.RenderStyle;
import com.mumfrey.worldeditcui.render.points.PointCube;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

/**
 * Draws the top and bottom circles around a cylindrical region
 * 
 * @author yetanotherx
 * @author Adam Mummery-Smith
 */
public class RenderCylinderBox extends RenderRegion
{
	private final double radX, radZ;
	private final int minY, maxY;
	private final double centreX, centreZ;
	
	public RenderCylinderBox(RenderStyle style, PointCube centre, double radX, double radZ, int minY, int maxY)
	{
		super(style);
		this.radX = radX;
		this.radZ = radZ;
		this.minY = minY;
		this.maxY = maxY;
		this.centreX = centre.getPoint().getX() + 0.5;
		this.centreZ = centre.getPoint().getZ() + 0.5;
	}
	
	@Override
	public void render(CUIRenderContext ctx)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();

		double xPos = this.centreX - ctx.cameraPos().getX();
		double zPos = this.centreZ - ctx.cameraPos().getZ();

		for (LineStyle line : this.style.getLines())
		{
			if (!line.prepare(this.style.getRenderType()))
			{
				continue;
			}
			
			double twoPi = Math.PI * 2;
			for (int yBlock : new int[] { this.minY, this.maxY + 1 })
			{
				buf.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
				line.applyColour(buf);

				for (int i = 0; i <= 75; i++)
				{
					double tempTheta = i * twoPi / 75;
					double tempX = this.radX * Math.cos(tempTheta);
					double tempZ = this.radZ * Math.sin(tempTheta);

					buf.vertex(xPos + tempX, yBlock - ctx.cameraPos().getY(), zPos + tempZ).next();
				}

				// And back to initial vertex (because we have to use LINE_STRIP rather than LINE_LOOP)
				buf.vertex(xPos + this.radX, yBlock - ctx.cameraPos().getY(), zPos).next();
				tessellator.draw();
			}
		}
	}
}
