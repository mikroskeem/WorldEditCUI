package com.mumfrey.worldeditcui.render.region;

import com.mumfrey.worldeditcui.WorldEditCUI;
import com.mumfrey.worldeditcui.config.ColourOption;
import com.mumfrey.worldeditcui.render.points.PointCube;
import com.mumfrey.worldeditcui.render.points.PointCubeTracking;
import com.mumfrey.worldeditcui.render.shapes.Render3DBox;
import com.mumfrey.worldeditcui.render.shapes.Render3DGrid;
import com.mumfrey.worldeditcui.util.BoundingBox;
import com.mumfrey.worldeditcui.util.Vector3;
import net.minecraft.entity.Entity;

/**
 * Main controller for a cuboid-type region
 *
 * @author yetanotherx
 * @author lahwran
 * @author Adam Mummery-Smith
 */
public class CuboidRegion extends Region
{
	private PointCube[] points = new PointCube[2];

	private Render3DGrid grid;
	private Render3DBox box;

	private double spacing = 1.0;

	public CuboidRegion(WorldEditCUI controller)
	{
		super(controller, ColourOption.CUBOIDBOX.style(), ColourOption.CUBOIDGRID.style(), ColourOption.CUBOIDPOINT1.style(), ColourOption.CUBOIDPOINT2.style());
	}

	@Override
	public void render(Vector3 cameraPos, float partialTicks)
	{
		if (this.points[0] != null && this.points[1] != null)
		{
			this.points[0].updatePoint(partialTicks);
			this.points[1].updatePoint(partialTicks);

			this.grid.render(cameraPos);
			this.box.render(cameraPos);

			this.points[0].render(cameraPos);
			this.points[1].render(cameraPos);
		}
		else if (this.points[0] != null)
		{
			this.points[0].updatePoint(partialTicks);
			this.points[0].render(cameraPos);
		}
		else if (this.points[1] != null)
		{
			this.points[1].updatePoint(partialTicks);
			this.points[1].render(cameraPos);
		}
	}

	@Override
	public void setGridSpacing(double spacing)
	{
		this.spacing = spacing;
		if (this.grid != null)
		{
			this.grid.setSpacing(spacing);
		}
	}

	@Override
	public void setCuboidPoint(int id, double x, double y, double z)
	{
		if (id < 2)
		{
			this.points[id] = new PointCube(x, y, z).setStyle(this.styles[id+2]);
		}

		this.updateBounds();
	}

	@Override
	public void setCuboidVertexLatch(int id, Entity entity, double traceDistance)
	{
		if (id < 2)
		{
			this.points[id] = new PointCubeTracking(entity, traceDistance).setStyle(this.styles[id+2]);
		}

		this.updateBounds();
	}

	private void updateBounds()
	{
		if (this.points[0] != null && this.points[1] != null)
		{
			BoundingBox bounds = new BoundingBox(this.points[0], this.points[1]);
			this.grid = new Render3DGrid(this.styles[1], bounds).setSpacing(this.spacing);
			this.box = new Render3DBox(this.styles[0], bounds);
		}
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

		if (this.points[0] != null)
		{
			this.points[0].setStyle(this.styles[2]);
		}

		if (this.points[1] != null)
		{
			this.points[1].setStyle(this.styles[3]);
		}
	}

	@Override
	public RegionType getType()
	{
		return RegionType.CUBOID;
	}
}
