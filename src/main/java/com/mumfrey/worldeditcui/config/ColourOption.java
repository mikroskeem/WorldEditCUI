package com.mumfrey.worldeditcui.config;

import com.mumfrey.worldeditcui.render.LineStyle;
import com.mumfrey.worldeditcui.render.RenderStyle;
import com.mumfrey.worldeditcui.render.RenderStyle.RenderType;
import net.minecraft.text.Text;

import static com.mumfrey.worldeditcui.WorldEditCUI.tr;

/**
 * Stores style data for each type of line.
 *
 * Each line has a normal line, and a hidden line.
 * The normal line has an alpha value of 0.8f, and
 * the hidden line has an alpha value of 0.2f. They
 * both have a thickness of 3.0f.
 *
 * @author yetanotherx
 * @author lahwran
 * @author Adam Mummery-Smith
 */
public enum ColourOption implements Option<Colour>
{
	CUBOIDBOX      ("cuboidedge", "cuboidEdgeColor", new Colour("#CC3333CC")),
	CUBOIDGRID     ("cuboidgrid", "cuboidGridColor", new Colour("#CC4C4CCC")),
	CUBOIDPOINT1   ("cuboidpoint1", "cuboidFirstPointColor", new Colour("#33CC33CC")),
	CUBOIDPOINT2   ("cuboidpoint2", "cuboidSecondPointColor", new Colour("#3333CCCC")),
	POLYGRID       ("polygrid", "polyGridColor", new Colour("#CC3333CC")),
	POLYBOX        ("polyedge", "polyEdgeColor", new Colour("#CC4C4CCC")),
	POLYPOINT      ("polypoint", "polyPointColor", new Colour("#33CCCCCC")),
	ELLIPSOIDGRID  ("ellipsoidgrid", "ellipsoidGridColor", new Colour("#CC4C4CCC")),
	ELLIPSOIDCENTRE("ellipsoidpoint", "ellipsoidPointColor", new Colour("#CCCC33CC")),
	CYLINDERGRID   ("cylindergrid", "cylinderGridColor", new Colour("#CC3333CC")),
	CYLINDERBOX    ("cylinderedge", "cylinderEdgeColor", new Colour("#CC4C4CCC")),
	CYLINDERCENTRE ("cylinderpoint", "cylinderPointColor", new Colour("#CC33CCCC")),
	CHUNKBOUNDARY  ("chunkboundary", "chunkBoundaryColor", new Colour("#33CC33CC")),
	CHUNKGRID      ("chunkgrid", "chunkGridColor", new Colour("#4CCCAA99"));

	/* package */ static final ColourOption[] VALUES = values();

    class Style implements RenderStyle
	{
		private RenderType renderType = RenderType.ANY;

		@Override
		public void setRenderType(RenderType renderType)
		{
			this.renderType = renderType;
		}

		@Override
		public RenderType getRenderType()
		{
			return this.renderType;
		}

		@Override
		public void setColour(Colour colour)
		{
		}

		@Override
		public Colour getColour()
		{
			return ColourOption.this.value();
		}

		@Override
		public LineStyle[] getLines()
		{
			return ColourOption.this.getLines();
		}
	}

	private final String configName;
	private final String displayName;
	private final Colour defaultColour;
	private Colour colour;
	private LineStyle normal, hidden;
	private final LineStyle[] lines = new LineStyle[2];

	ColourOption(String displayName, String configName, Colour colour)
	{
	    this.configName = configName;
		this.displayName = displayName;
		this.colour = colour;
		this.defaultColour = new Colour().copyFrom(colour);
		this.updateLines();
	}

    @Override
    public String key() {
        return this.configName;
    }

    @Override
    public Class<Colour> type() {
        return Colour.class;
    }

    @Override
    public Colour defaultValue() {
        return this.defaultColour;
    }

    @Override
    public Text label() {
        return tr("color." + displayName);
    }


    public RenderStyle style()
	{
		return new Style();
	}

	public void value(Colour colour)
	{
		this.colour = colour;
		this.updateLines();
	}

	@Override
	public Colour value()
	{
		return this.colour;
	}

	public LineStyle getHidden()
	{
		return this.hidden;
	}

	public LineStyle getNormal()
	{
		return this.normal;
	}

	public LineStyle[] getLines()
	{
		return this.lines;
	}

	public void setDefault()
	{
		this.colour.copyFrom(this.defaultColour);
		this.updateLines();
	}

	public Colour getDefault()
	{
		return this.defaultColour;
	}

	public void setColourIntRGBA(int argb)
	{
		int rgba = ((argb << 8) & 0xFFFFFF00) | (((argb & 0xFF000000) >> 24) & 0xFF);
		this.colour.setHex(Integer.toHexString(rgba));
		this.updateLines();
	}

	public int getColourIntARGB()
	{
		return this.colour.getIntARGB();
	}

	private void updateLines()
	{
		this.lines[1] = this.normal = new LineStyle(RenderType.VISIBLE, 3.0f, this.colour.red(), this.colour.green(), this.colour.blue(), this.colour.alpha());
		this.lines[0] = this.hidden = new LineStyle(RenderType.HIDDEN, 3.0f, this.colour.red() * 0.75F, this.colour.green() * 0.75F, this.colour.blue() * 0.75F, this.colour.alpha() * 0.25F);
	}
}
