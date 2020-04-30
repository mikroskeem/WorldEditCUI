package com.mumfrey.worldeditcui.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mumfrey.worldeditcui.InitialisationFactory;
import com.mumfrey.worldeditcui.render.ConfiguredColour;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.I18n;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores and reads WorldEditCUI settings
 * 
 * @author yetanotherx
 * @author Adam Mummery-Smith
 * @author Jesús Sanz - Modified to work with the config GUI implementation
 */
public final class CUIConfiguration implements InitialisationFactory
{
	private static final String CONFIG_FILE_NAME = "worldeditcui.config.json";

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@Expose private boolean debugMode = false;
	@Expose private boolean ignoreUpdates = false;
	@Expose private boolean promiscuous = false;
	@Expose private boolean alwaysOnTop = false;
	@Expose private boolean clearAllOnKey = false;
	
	@Expose private Colour cuboidGridColor        = ConfiguredColour.CUBOIDBOX.getDefault();
	@Expose private Colour cuboidEdgeColor        = ConfiguredColour.CUBOIDGRID.getDefault();
	@Expose private Colour cuboidFirstPointColor  = ConfiguredColour.CUBOIDPOINT1.getDefault();
	@Expose private Colour cuboidSecondPointColor = ConfiguredColour.CUBOIDPOINT2.getDefault();
	@Expose private Colour polyGridColor          = ConfiguredColour.POLYGRID.getDefault();
	@Expose private Colour polyEdgeColor          = ConfiguredColour.POLYBOX.getDefault();
	@Expose private Colour polyPointColor         = ConfiguredColour.POLYPOINT.getDefault();
	@Expose private Colour ellipsoidGridColor     = ConfiguredColour.ELLIPSOIDGRID.getDefault();
	@Expose private Colour ellipsoidPointColor    = ConfiguredColour.ELLIPSOIDCENTRE.getDefault();
	@Expose private Colour cylinderGridColor      = ConfiguredColour.CYLINDERGRID.getDefault();
	@Expose private Colour cylinderEdgeColor      = ConfiguredColour.CYLINDERBOX.getDefault();
	@Expose private Colour cylinderPointColor     = ConfiguredColour.CYLINDERCENTRE.getDefault();
	@Expose private Colour chunkBoundaryColour    = ConfiguredColour.CHUNKBOUNDARY.getDefault();
	@Expose private Colour chunkGridColour        = ConfiguredColour.CHUNKGRID.getDefault();

	private static Map<String, Object> configArray = new HashMap<String, Object>();
	
	/**
	 * Copies the default config file to the proper directory if it does not
	 * exist. It then reads the file and sets each variable to the proper value.
	 */
	@Override
	public void initialise()
	{
		int index = 0;
		try
		{
			for (Field field : this.getClass().getDeclaredFields())
			{
				if (field.getType() == Colour.class)
				{
					ConfiguredColour configuredColour = ConfiguredColour.values()[index++];
					Colour colour = Colour.firstOrDefault((Colour)field.get(this), configuredColour.getColour().getHex());
					field.set(this, colour);
					configuredColour.setColour(colour);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		this.save();
	}
	
	public boolean isDebugMode()
	{
		return this.debugMode;
	}
	
	public boolean ignoreUpdates()
	{
		return this.ignoreUpdates;
	}
	
	public boolean isPromiscuous()
	{
		return this.promiscuous;
	}
	
	public void setPromiscuous(boolean promiscuous)
	{
		this.promiscuous = promiscuous;
	}
	
	public boolean isAlwaysOnTop()
	{
		return this.alwaysOnTop;
	}
	
	public void setAlwaysOnTop(boolean alwaysOnTop)
	{
		this.alwaysOnTop = alwaysOnTop;
	}
	
	public boolean isClearAllOnKey()
	{
		return this.clearAllOnKey;
	}
	
	public void setClearAllOnKey(boolean clearAllOnKey)
	{
		this.clearAllOnKey = clearAllOnKey;
	}

	public static CUIConfiguration create()
	{
		File jsonFile = new File(FabricLoader.getInstance().getConfigDirectory(), CUIConfiguration.CONFIG_FILE_NAME);

		if (jsonFile.exists())
		{
			FileReader fileReader = null;

			try
			{
				fileReader = new FileReader(jsonFile);
				CUIConfiguration config = CUIConfiguration.GSON.fromJson(fileReader, CUIConfiguration.class);
				configArray.put("debugMode", config.debugMode);
				configArray.put("ignoreUpdates", config.ignoreUpdates);
				configArray.put("promiscuous", config.promiscuous);
				configArray.put("alwaysOnTop", config.alwaysOnTop);
				configArray.put("clearAllOnKey", config.clearAllOnKey);

				configArray.put("cuboidGridColor", config.cuboidGridColor);
				configArray.put("cuboidEdgeColor", config.cuboidEdgeColor);
				configArray.put("cuboidFirstPointColor", config.cuboidFirstPointColor);
				configArray.put("cuboidSecondPointColor", config.cuboidSecondPointColor);
				configArray.put("polyGridColor", config.polyGridColor);
				configArray.put("polyEdgeColor", config.polyEdgeColor);
				configArray.put("polyPointColor", config.polyPointColor);
				configArray.put("ellipsoidGridColor", config.ellipsoidGridColor);
				configArray.put("ellipsoidPointColor", config.ellipsoidPointColor);
				configArray.put("cylinderGridColor", config.cylinderGridColor);
				configArray.put("cylinderEdgeColor", config.cylinderEdgeColor);
				configArray.put("cylinderPointColor", config.cylinderPointColor);
				configArray.put("chunkBoundaryColour", config.chunkBoundaryColour);
				configArray.put("chunkGridColour", config.chunkGridColour);

				return config;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				try
				{
					if (fileReader != null) fileReader.close();
				}
				catch (IOException ex) {}
			}
		}

		return new CUIConfiguration();
	}

	public void changeValue(String text, Object value) {
		configArray.replace(text, value);
	}

	public Map<String, Object> getConfigArray() {
		return configArray;
	}

	public void configChanged() {
		debugMode 				= (Boolean) configArray.get("debugMode");
		ignoreUpdates 			= (Boolean) configArray.get("ignoreUpdates");
		promiscuous 			= (Boolean) configArray.get("promiscuous");
		alwaysOnTop 			= (Boolean) configArray.get("alwaysOnTop");
		clearAllOnKey 			= (Boolean) configArray.get("clearAllOnKey");

		cuboidGridColor 		= (Colour) 	configArray.get("cuboidGridColor");
		cuboidEdgeColor 		= (Colour) 	configArray.get("cuboidEdgeColor");
		cuboidFirstPointColor 	= (Colour) 	configArray.get("cuboidFirstPointColor");
		cuboidSecondPointColor 	= (Colour) 	configArray.get("cuboidSecondPointColor");
		polyGridColor 			= (Colour) 	configArray.get("polyGridColor");
		polyEdgeColor 			= (Colour) 	configArray.get("polyEdgeColor");
		polyPointColor 			= (Colour) 	configArray.get("polyPointColor");
		ellipsoidGridColor 		= (Colour) 	configArray.get("ellipsoidGridColor");
		ellipsoidPointColor 	= (Colour) 	configArray.get("ellipsoidPointColor");
		cylinderGridColor 		= (Colour) 	configArray.get("cylinderGridColor");
		cylinderEdgeColor 		= (Colour) 	configArray.get("cylinderEdgeColor");
		cylinderPointColor 		= (Colour) 	configArray.get("cylinderPointColor");
		chunkBoundaryColour 	= (Colour) 	configArray.get("chunkBoundaryColour");
		chunkGridColour 		= (Colour) 	configArray.get("chunkGridColour");
		this.save();
	}

	public Object getDefaultValue(String text) {
		switch(text) {
			case "debugMode":
			case "ignoreUpdates":
			case "promiscuous":
			case "alwaysOnTop":
			case "clearAllOnKey": return false;

			case "cuboidGridColor": return ConfiguredColour.CUBOIDBOX.getDefault();
			case "cuboidEdgeColor": return ConfiguredColour.CUBOIDGRID.getDefault();
			case "cuboidFirstPointColor": return ConfiguredColour.CUBOIDPOINT1.getDefault();
			case "cuboidSecondPointColor": return ConfiguredColour.CUBOIDPOINT2.getDefault();
			case "polyGridColor": return ConfiguredColour.POLYGRID.getDefault();
			case "polyEdgeColor": return ConfiguredColour.POLYBOX.getDefault();
			case "polyPointColor": return ConfiguredColour.POLYPOINT.getDefault();
			case "ellipsoidGridColor": return ConfiguredColour.ELLIPSOIDGRID.getDefault();
			case "ellipsoidPointColor": return ConfiguredColour.ELLIPSOIDCENTRE.getDefault();
			case "cylinderGridColor": return ConfiguredColour.CYLINDERGRID.getDefault();
			case "cylinderEdgeColor": return ConfiguredColour.CYLINDERBOX.getDefault();
			case "cylinderPointColor": return ConfiguredColour.CYLINDERCENTRE.getDefault();
			case "chunkBoundaryColour": return ConfiguredColour.CHUNKBOUNDARY.getDefault();
			case "chunkGridColour": return ConfiguredColour.CHUNKGRID.getDefault();
		}

		return null;
	}

	public String getDefaultDisplayName(String text) {
		switch(text) {
			case "debugMode": return I18n.translate("worldeditcui.options.debugMode");
			case "ignoreUpdates": return I18n.translate("worldeditcui.options.ignoreUpdates");
			case "promiscuous": return I18n.translate("worldeditcui.options.compat.spammy");
			case "alwaysOnTop": return I18n.translate("worldeditcui.options.compat.ontop");
			case "clearAllOnKey": return I18n.translate("worldeditcui.options.extra.clearall");

			case "cuboidGridColor": return I18n.translate("worldeditcui.color.cuboidgrid");
			case "cuboidEdgeColor": return I18n.translate("worldeditcui.color.cuboidedge");
			case "cuboidFirstPointColor": return I18n.translate("worldeditcui.color.cuboidpoint1");
			case "cuboidSecondPointColor": return I18n.translate("worldeditcui.color.cuboidpoint2");
			case "polyGridColor": return I18n.translate("worldeditcui.color.polygrid");
			case "polyEdgeColor": return I18n.translate("worldeditcui.color.polyedge");
			case "polyPointColor": return I18n.translate("worldeditcui.color.polypoint");
			case "ellipsoidGridColor": return I18n.translate("worldeditcui.color.ellipsoidgrid");
			case "ellipsoidPointColor": return I18n.translate("worldeditcui.color.ellipsoidpoint");
			case "cylinderGridColor": return I18n.translate("worldeditcui.color.cylindergrid");
			case "cylinderEdgeColor": return I18n.translate("worldeditcui.color.cylinderedge");
			case "cylinderPointColor": return I18n.translate("worldeditcui.color.cylinderpoint");
			case "chunkBoundaryColour": return I18n.translate("worldeditcui.color.chunkboundary");
			case "chunkGridColour": return I18n.translate("worldeditcui.color.chunkgrid");
		}

		return null;
	}

	public void save()
	{
		File jsonFile = new File(FabricLoader.getInstance().getConfigDirectory(), CUIConfiguration.CONFIG_FILE_NAME);
		
		FileWriter fileWriter = null;
		
		try
		{
			fileWriter = new FileWriter(jsonFile);
			CUIConfiguration.GSON.toJson(this, fileWriter);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if (fileWriter != null) fileWriter.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
}
