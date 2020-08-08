package com.mumfrey.worldeditcui.render.region;

import com.mumfrey.worldeditcui.WorldEditCUI;
import com.mumfrey.worldeditcui.config.ColourOption;
import com.mumfrey.worldeditcui.render.points.PointCube;
import com.mumfrey.worldeditcui.render.shapes.RenderCylinderBox;
import com.mumfrey.worldeditcui.render.shapes.RenderCylinderCircles;
import com.mumfrey.worldeditcui.render.shapes.RenderCylinderGrid;
import com.mumfrey.worldeditcui.util.Vector3;

/**
 * Main controller for a cylinder-type region
 *
 * @author yetanotherx
 * @author Adam Mummery-Smith
 */
public class CylinderRegion extends Region
{
	private PointCube centre;
	private double radX = 0, radZ = 0;
	private int minY = 0, maxY = 0;

	private RenderCylinderCircles circles;
	private RenderCylinderGrid grid;
	private RenderCylinderBox box;

	public CylinderRegion(WorldEditCUI controller)
	{
		super(controller, ColourOption.CYLINDERBOX.style(), ColourOption.CYLINDERGRID.style(), ColourOption.CYLINDERCENTRE.style());
	}

	@Override
	public void render(Vector3 cameraPos, float partialTicks)
	{
		if (this.centre != null)
		{
			this.centre.render(cameraPos);
			this.circles.render(cameraPos);
			this.grid.render(cameraPos);
			this.box.render(cameraPos);
		}
	}

	@Override
	public void setCylinderCenter(int x, int y, int z)
	{
		this.centre = new PointCube(x, y, z);
		this.centre.setStyle(this.styles[2]);
		this.update();
	}

	@Override
	public void setCylinderRadius(double x, double z)
	{
		this.radX = x;
		this.radZ = z;
		this.update();
	}

	@Override
	public void setMinMax(int min, int max)
	{
		this.minY = min;
		this.maxY = max;
		this.update();
	}

	private void update()
	{
		int tMin = this.minY;
		int tMax = this.maxY;

		if (this.minY == 0 || this.maxY == 0)
		{
			tMin = (int)this.centre.getPoint().getY();
			tMax = (int)this.centre.getPoint().getY();
		}

		this.circles = new RenderCylinderCircles(this.styles[1], this.centre, this.radX, this.radZ, tMin, tMax);
		this.grid = new RenderCylinderGrid(this.styles[1], this.centre, this.radX, this.radZ, tMin, tMax);
		this.box = new RenderCylinderBox(this.styles[0], this.centre, this.radX, this.radZ, tMin, tMax);
	}

	@Override
	protected void updateStyles()
	{
		if (this.box != null)
		{
			this.box.setStyle(this.styles[0]);
		}

		if (this.grid != null)
		{
			this.grid.setStyle(this.styles[1]);
		}

		if (this.circles != null)
		{
			this.circles.setStyle(this.styles[1]);
		}

		if (this.centre != null)
		{
			this.centre.setStyle(this.styles[2]);
		}
	}

	@Override
	public RegionType getType()
	{
		return RegionType.CYLINDER;
	}
}
