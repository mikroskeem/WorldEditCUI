package com.mumfrey.worldeditcui.config;

/**
 * @author Adam Mummery-Smith
 */
public class Colour
{
	private String hex;
	private transient int argb;
	private transient float a, r, g, b;
	private transient String defaultColour;
	private transient boolean woken;
	
	public Colour(String colour)
	{
		this.hex = this.defaultColour = colour;
		if(this.hex.length() == 9)
			this.update();
	}
	
	public Colour()
	{
	}
	
	public static Colour parse(String colour, Colour defaultColour)
	{
		return ((colour = Colour.sanitiseColour(colour, null)) == null) ? defaultColour : new Colour(colour);
	}
	
	public static Colour firstOrDefault(Colour colour, String defaultColour)
	{
		if (colour == null)
		{
			return new Colour(defaultColour);
		}
		
		if (colour.hex == null)
		{
			colour.hex = defaultColour;
			colour.defaultColour = defaultColour;
		}
		else
		{
			colour.hex = Colour.sanitiseColour(colour.hex, defaultColour);
		}
		
		return colour;
	}
	
	/**
	 * Validates a user-entered colour code. Ensures that style is not null, it
	 * starts with #, that it has all 6 digits, and that each hex code is valid.
	 * 
	 * @param colour
	 * @param def
	 * @return
	 */
	private static String sanitiseColour(String colour, String def)
	{
		if (colour == null)
		{
			return def;
		}
		else if (!colour.startsWith("#"))
		{
			return def;
		}
		else if (colour.length() != 7 && colour.length() != 9)
		{
			return def;
		}
		
		return (colour.matches("(?i)^#[0-9a-f]{6,8}$")) ? colour : def;
	}
	
	public void setHex(String hex)
	{
		if (hex.length() < 8)
		{
			hex = "00000000".substring(0, 8 - hex.length()) + hex;
		}
		
		this.hex = "#" + hex;
		this.update();
	}
	
	public String getHex()
	{
		if (this.hex == null)
		{
			this.hex = this.defaultColour;
			this.update();
		}
		
		if (this.hex.length() == 7)
		{
			this.hex = this.hex + "CC";
			this.update();
		}
		
		return this.hex;
	}
	
	private void update()
	{
		String hex = this.getHex();
		this.argb = (int)Long.parseLong(hex.substring(7, 9) + hex.substring(1, 7), 16);
		this.r = ((Integer.parseInt(hex.substring(1, 3), 16)) / 256.0F);
		this.g = ((Integer.parseInt(hex.substring(3, 5), 16)) / 256.0F);
		this.b = ((Integer.parseInt(hex.substring(5, 7), 16)) / 256.0F);
		this.a = ((Integer.parseInt(hex.substring(7, 9), 16)) / 256.0F);
		this.woken = true;
	}
	
	public int getIntARGB()
	{
		return this.argb;
	}
	
	public float red()
	{
		if (!this.woken)
		{
			this.update();
		}
		
		return this.r;
	}
	
	public float green()
	{
		if (!this.woken)
		{
			this.update();
		}
		
		return this.g;
	}
	
	public float blue()
	{
		if (!this.woken)
		{
			this.update();
		}
		
		return this.b;
	}
	
	public float alpha()
	{
		if (!this.woken)
		{
			this.update();
		}
		
		return this.a;
	}
	
	public Colour copyFrom(Colour other)
	{
		this.hex = other.getHex();
		this.update();
		return this;
	}
}
