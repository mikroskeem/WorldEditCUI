package com.mumfrey.worldeditcui.debug;

import com.mumfrey.worldeditcui.InitialisationFactory;
import com.mumfrey.worldeditcui.WorldEditCUI;
import com.mumfrey.worldeditcui.config.FlagOption;
import com.mumfrey.worldeditcui.exceptions.InitialisationException;
import com.mumfrey.worldeditcui.util.ConsoleLogFormatter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Debugging helper class
 *
 * @author yetanotherx
 * @author Adam Mummery-Smith
 */
public final class CUIDebug implements InitialisationFactory
{
	private static final Logger logger = Logger.getLogger("WorldEditCUI");

	private final WorldEditCUI controller;
	private File debugFile;
	private boolean debugMode = false;

	public CUIDebug(WorldEditCUI controller)
	{
		this.controller = controller;
	}

	@Override
	public void initialise() throws InitialisationException
	{

		ConsoleLogFormatter formatter = new ConsoleLogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);

		CUIDebug.logger.setUseParentHandlers(false);
		CUIDebug.logger.addHandler(handler);

		try
		{
			this.debugFile = new File(FabricLoader.getInstance().getGameDirectory(), "worldeditcui.debug.log");
			this.debugMode = FlagOption.DEBUG_MODE.value();

			if (this.debugMode)
			{
				FileHandler newHandler = new FileHandler(this.debugFile.getAbsolutePath());
				newHandler.setFormatter(formatter);
				CUIDebug.logger.addHandler(newHandler);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
			throw new InitialisationException();
		}

	}

	/**
	 * Shows a message if debug mode is true
	 * @param message message to show
	 */
	public void debug(String message)
	{
		if (this.debugMode)
		{
			CUIDebug.logger.info("WorldEditCUI Debug - " + message);
		}
	}

	public void info(String message)
	{
		CUIDebug.logger.info(message);
	}

	public void info(String message, Throwable e)
	{
		CUIDebug.logger.log(Level.INFO, message, e);
	}
}
