package com.mumfrey.worldeditcui.render.shapes;

import com.mumfrey.worldeditcui.event.listeners.CUIRenderContext;
import com.mumfrey.worldeditcui.render.LineStyle;
import com.mumfrey.worldeditcui.render.RenderStyle;
import com.mumfrey.worldeditcui.util.BoundingBox;
import com.mumfrey.worldeditcui.util.Observable;
import com.mumfrey.worldeditcui.util.Vector3;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

/**
 * Draws a rectangular prism around 2 corners
 * 
 * @author yetanotherx
 * @author lahwran
 * @author Adam Mummery-Smith
 */
public class Render3DBox extends RenderRegion
{
	private Vector3 first, second;
	
	public Render3DBox(RenderStyle style, BoundingBox region)
	{
		this(style, region.getMin(), region.getMax());
		if (region.isDynamic())
		{
			region.addObserver(this);
		}
	}
	
	public Render3DBox(RenderStyle style, Vector3 first, Vector3 second)
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
		
		for (LineStyle line : this.style.getLines())
		{
			if (!line.prepare(this.style.getRenderType()))
			{
				continue;
			}
			
			// Draw bottom face
			buf.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
			line.applyColour(buf);
			buf.vertex(x1, y1, z1).next();
			buf.vertex(x2, y1, z1).next();
			buf.vertex(x2, y1, z2).next();
			buf.vertex(x1, y1, z2).next();
			buf.vertex(x1, y1, z1).next(); // Loop back for LINE_STRIP
			tessellator.draw();
			
			// Draw top face
			buf.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
			line.applyColour(buf);
			buf.vertex(x1, y2, z1).next();
			buf.vertex(x2, y2, z1).next();
			buf.vertex(x2, y2, z2).next();
			buf.vertex(x1, y2, z2).next();
			buf.vertex(x1, y2, z1).next(); // Loop back for LINE_STRIP
			tessellator.draw();
			
			// Draw join top and bottom faces
			buf.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
			line.applyColour(buf);

			buf.vertex(x1, y1, z1).next();
			buf.vertex(x1, y2, z1).next();
			
			buf.vertex(x2, y1, z1).next();
			buf.vertex(x2, y2, z1).next();
			
			buf.vertex(x2, y1, z2).next();
			buf.vertex(x2, y2, z2).next();
			
			buf.vertex(x1, y1, z2).next();
			buf.vertex(x1, y2, z2).next();
			
			tessellator.draw();
		}
	}
}
