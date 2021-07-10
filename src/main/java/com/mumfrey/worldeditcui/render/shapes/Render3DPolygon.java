package com.mumfrey.worldeditcui.render.shapes;

import com.mumfrey.worldeditcui.event.listeners.CUIRenderContext;
import com.mumfrey.worldeditcui.render.LineStyle;
import com.mumfrey.worldeditcui.render.RenderStyle;
import com.mumfrey.worldeditcui.util.Vector3;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

/**
 * Draws a polygon
 * 
 * @author yetanotherx
 * @author lahwran
 * @author Adam Mummery-Smith
 */
public class Render3DPolygon extends RenderRegion
{
	private final Vector3[] vertices;
	
	public Render3DPolygon(RenderStyle style, Vector3... vertices)
	{
		super(style);
		this.vertices = vertices;
	}
	
	@Override
	public void render(CUIRenderContext ctx)
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
			for (int i = 0; i < this.vertices.length + 1; i++) // Loop around by one vertex to compensate for LINE_STRIP instead of LINE_LOOP
			{
				final Vector3 vertex = this.vertices[i % this.vertices.length];
				buf.vertex(vertex.getX() - ctx.cameraPos().getX(), vertex.getY() - ctx.cameraPos().getY(), vertex.getZ() - ctx.cameraPos().getZ()).next();
			}
			tessellator.draw();
		}
	}
}
