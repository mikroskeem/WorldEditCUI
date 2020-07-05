package com.mumfrey.worldeditcui.render.points;

import com.mumfrey.worldeditcui.config.ColourOption;
import com.mumfrey.worldeditcui.render.RenderStyle;
import com.mumfrey.worldeditcui.render.shapes.Render3DBox;
import com.mumfrey.worldeditcui.util.BoundingBox;
import com.mumfrey.worldeditcui.util.Observable;
import com.mumfrey.worldeditcui.util.Vector3;

/**
 * Stores data about a cube surrounding a block in the world. Used to store info
 * about the selector blocks. Keeps track of colour, x/y/z values, and rendering
 *
 * @author yetanotherx
 * @author lahwran
 * @author Adam Mummery-Smith
 */
public class PointCube extends Observable<BoundingBox>
{
	private static final double PADDING = 0.03;

	protected static final Vector3 MIN_VEC = new Vector3(PointCube.PADDING, PointCube.PADDING, PointCube.PADDING);
	protected static final Vector3 MAX_VEC = new Vector3(PointCube.PADDING + 1, PointCube.PADDING + 1, PointCube.PADDING + 1);

	protected int id;
	protected Vector3 point;
	protected RenderStyle style = ColourOption.CUBOIDPOINT1.style();

	protected Render3DBox box;

	public PointCube(double x, double y, double z)
	{
		this(new Vector3(x, y, z));
	}

	public PointCube(Vector3 point)
	{
		this.setPoint(point);
	}

	public boolean isDynamic()
	{
		return false;
	}

	public PointCube setId(int id)
	{
		this.id = id;
		return this;
	}

	public int getId()
	{
		return this.id;
	}

	public void render(Vector3 cameraPos)
	{
		this.box.render(cameraPos);
	}

	public void updatePoint(float partialTicks)
	{
	}

	public Vector3 getPoint()
	{
		return this.point;
	}

	public void setPoint(Vector3 point)
	{
		this.point = point;
		this.update();
	}

	public RenderStyle getStyle()
	{
		return this.style;
	}

	public PointCube setStyle(RenderStyle style)
	{
		this.style = style;
		this.update();
		return this;
	}

	private void update()
	{
		this.box = new Render3DBox(this.style, this.point.subtract(PointCube.MIN_VEC), this.point.add(PointCube.MAX_VEC));
	}
}
