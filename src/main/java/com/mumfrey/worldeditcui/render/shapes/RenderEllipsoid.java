package com.mumfrey.worldeditcui.render.shapes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mumfrey.worldeditcui.event.listeners.CUIRenderContext;
import com.mumfrey.worldeditcui.render.LineStyle;
import com.mumfrey.worldeditcui.render.RenderStyle;
import com.mumfrey.worldeditcui.render.points.PointCube;
import com.mumfrey.worldeditcui.util.Vector3;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

/**
 * Draws an ellipsoid shape around a centre point.
 * 
 * @author yetanotherx
 * @author Adam Mummery-Smith
 */
public class RenderEllipsoid extends RenderRegion
{

	protected final static double TAU = Math.PI * 2.0;
	/**
	 * The number of intervals to draw in around the ellipsoid.
	 */
	protected static final double SUBDIVISIONS = 40;
	
	protected PointCube centre;
	private final Vector3 radii;
	
	protected final double centreX, centreY, centreZ;
	
	public RenderEllipsoid(RenderStyle style, PointCube centre, Vector3 radii)
	{
		super(style);
		this.centre = centre;
		this.radii = radii;
		this.centreX = centre.getPoint().getX() + 0.5;
		this.centreY = centre.getPoint().getY() + 0.5;
		this.centreZ = centre.getPoint().getZ() + 0.5;
	}
	
	@Override
	public void render(CUIRenderContext ctx)
	{
		ctx.matrices().push();
		ctx.matrices().translate(this.centreX - ctx.cameraPos().getX(), this.centreY - ctx.cameraPos().getY(), this.centreZ - ctx.cameraPos().getZ());
		ctx.applyMatrices();

		for (LineStyle line : this.style.getLines())
		{
			if (line.prepare(this.style.getRenderType()))
			{
				this.drawXZPlane(line);
				this.drawYZPlane(line);
				this.drawXYPlane(line);
			}
		}

		ctx.matrices().pop();
		ctx.applyMatrices();
	}
	
	protected void drawXZPlane(LineStyle line)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();

		int yRad = (int)Math.floor(this.radii.getY());
		for (int yBlock = -yRad; yBlock < yRad; yBlock++)
		{
			buf.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
			line.applyColour(buf);

			for (int i = 0; i <= SUBDIVISIONS + 1; i++)
			{
				double tempTheta = (i % (SUBDIVISIONS + 1)) * TAU / SUBDIVISIONS; // overlap by one for LINE_STRIP
				double tempX = this.radii.getX() * Math.cos(tempTheta) * Math.cos(Math.asin(yBlock / this.radii.getY()));
				double tempZ = this.radii.getZ() * Math.sin(tempTheta) * Math.cos(Math.asin(yBlock / this.radii.getY()));
				
				buf.vertex(tempX, yBlock, tempZ).next();
			}
			tessellator.draw();
		}
		
		buf.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
		line.applyColour(buf);

		for (int i = 0; i <= SUBDIVISIONS + 1; i++)
		{
			double tempTheta = (i % (SUBDIVISIONS + 1)) * TAU / SUBDIVISIONS;
			double tempX = this.radii.getX() * Math.cos(tempTheta);
			double tempZ = this.radii.getZ() * Math.sin(tempTheta);
			
			buf.vertex(tempX, 0.0, tempZ).next();
		}
		tessellator.draw();
	}
	
	protected void drawYZPlane(LineStyle line)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();

		int xRad = (int)Math.floor(this.radii.getX());
		for (int xBlock = -xRad; xBlock < xRad; xBlock++)
		{
			buf.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);
			line.applyColour(buf);

			for (int i = 0; i <= SUBDIVISIONS + 1; i++)
			{
				double tempTheta = (i % (SUBDIVISIONS + 1)) * TAU / SUBDIVISIONS;
				double tempY = this.radii.getY() * Math.cos(tempTheta) * Math.sin(Math.acos(xBlock / this.radii.getX()));
				double tempZ = this.radii.getZ() * Math.sin(tempTheta) * Math.sin(Math.acos(xBlock / this.radii.getX()));
				
				buf.vertex(xBlock, tempY, tempZ).next();
			}
			tessellator.draw();
		}
		
		buf.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);
		line.applyColour(buf);

		for (int i = 0; i <= SUBDIVISIONS + 1; i++)
		{
			double tempTheta = (i % (SUBDIVISIONS + 1)) * TAU / SUBDIVISIONS; // override by one for LINE_STRIP
			double tempY = this.radii.getY() * Math.cos(tempTheta);
			double tempZ = this.radii.getZ() * Math.sin(tempTheta);
			
			buf.vertex(0.0, tempY, tempZ).next();
		}
		tessellator.draw();
	}
	
	protected void drawXYPlane(LineStyle line)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();

		int zRad = (int)Math.floor(this.radii.getZ());
		for (int zBlock = -zRad; zBlock < zRad; zBlock++)
		{
			buf.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);
			line.applyColour(buf);

			for (int i = 0; i <= SUBDIVISIONS + 1; i++)
			{
				double tempTheta = (i % (SUBDIVISIONS + 1)) * TAU / SUBDIVISIONS; // overlap by one for LINE_STRIP
				double tempX = this.radii.getX() * Math.sin(tempTheta) * Math.sin(Math.acos(zBlock / this.radii.getZ()));
				double tempY = this.radii.getY() * Math.cos(tempTheta) * Math.sin(Math.acos(zBlock / this.radii.getZ()));
				
				buf.vertex(tempX, tempY, zBlock).next();
			}
			tessellator.draw();
		}
		
		buf.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);
		line.applyColour(buf);

		for (int i = 0; i <= SUBDIVISIONS + 1; i++)
		{
			double tempTheta = (i % (SUBDIVISIONS + 1)) * TAU / SUBDIVISIONS; // overlap by one for LINE_STRIP
			double tempX = this.radii.getX() * Math.cos(tempTheta);
			double tempY = this.radii.getY() * Math.sin(tempTheta);
			
			buf.vertex(tempX, tempY, 0.0).next();
		}
		tessellator.draw();
	}
}
