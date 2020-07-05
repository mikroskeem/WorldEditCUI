package com.mumfrey.worldeditcui.render.points;

import com.mumfrey.worldeditcui.config.ColourOption;
import com.mumfrey.worldeditcui.render.RenderStyle;
import com.mumfrey.worldeditcui.render.shapes.Render3DBox;
import com.mumfrey.worldeditcui.util.Vector2;
import com.mumfrey.worldeditcui.util.Vector3;

/**
 * Stores data about a prism surrounding two blocks in the world. Used to store
 * info about the selector blocks for polys. Keeps track of colour, x/y/z
 * values, and rendering.
 *
 * @author yetanotherx
 * @author lahwran
 * @author Adam Mummery-Smith
 */
public class PointRectangle
{
	private static final double OFF = 0.03;
	private static final Vector2 MIN_VEC = new Vector2(PointRectangle.OFF, PointRectangle.OFF);
	private static final Vector2 MAX_VEC = new Vector2(PointRectangle.OFF + 1, PointRectangle.OFF + 1);

	protected Vector2 point;
	protected RenderStyle style = ColourOption.POLYPOINT.style();

	private int min, max;

	private Render3DBox box;

	public PointRectangle(int x, int z)
	{
		this(new Vector2(x, z));
	}

	public PointRectangle(Vector2 point)
	{
		this.setPoint(point);
	}

	public void render(Vector3 cameraPos)
	{
		this.box.render(cameraPos);
	}

	public Vector2 getPoint()
	{
		return this.point;
	}

	public void setPoint(Vector2 point)
	{
		this.point = point;
	}

	public RenderStyle getStyle()
	{
		return this.style;
	}

	public void setStyle(RenderStyle style)
	{
		this.style = style;
	}

	public void setMinMax(int min, int max)
	{
		this.min = min;
		this.max = max;
		this.update();
	}

	public int getMin()
	{
		return this.min;
	}

	public int getMax()
	{
		return this.max;
	}

	private void update()
	{
		this.box = new Render3DBox(this.style, this.point.subtract(PointRectangle.MIN_VEC).toVector3(this.min - 0.03f), this.point.add(PointRectangle.MAX_VEC).toVector3(this.max + 1 + 0.03f));
	}
}
