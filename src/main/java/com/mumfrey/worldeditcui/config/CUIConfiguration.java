package com.mumfrey.worldeditcui.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mumfrey.worldeditcui.InitialisationFactory;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
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
	private static final Logger LOGGER = LogManager.getLogger();

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final Map<String, Option<?>> knownFields = new LinkedHashMap<>();

	private CUIConfiguration() {
	    register(FlagOption.VALUES);
	    register(ColourOption.VALUES);
    }

    @SafeVarargs
    private final <T extends Option<V>, V> void register(T... options) {
	    for (T option : options) {
	        this.knownFields.put(option.key(), option);
	        option.value(option.defaultValue());
        }
    }

	/**
	 * Copies the default config file to the proper directory if it does not
	 * exist. It then reads the file and sets each variable to the proper value.
	 */
	@Override
	public void initialise()
	{
		this.save();
	}

	private static File getConfigFile()
	{
		return new File(FabricLoader.getInstance().getConfigDirectory(), CUIConfiguration.CONFIG_FILE_NAME);
	}

	public static CUIConfiguration create()
	{
		File jsonFile = getConfigFile();

		CUIConfiguration config = new CUIConfiguration();
		if (jsonFile.exists())
		{
			try (Reader fileReader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8); JsonReader jsonReader = GSON.newJsonReader(fileReader))
			{
			    jsonReader.beginObject();
			    while (jsonReader.peek() != JsonToken.END_OBJECT) {
			        final String key = jsonReader.nextName();
			        final Option<?> optionType = config.knownFields.get(key);
			        if (optionType == null) {
			            LOGGER.warn("Unknown configuration field: {}", key);
			            jsonReader.skipValue();
			            continue;
                    }
			        readOption(optionType, jsonReader);
                }
			    jsonReader.endObject();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return config;
	}

	private static <T> void readOption(Option<T> option, JsonReader json) throws IOException {
        option.value(GSON.getAdapter(option.type()).read(json));
    }

    public void save()
	{
		try(Writer fileWriter = new OutputStreamWriter(new FileOutputStream(getConfigFile()), StandardCharsets.UTF_8); JsonWriter json = GSON.newJsonWriter(fileWriter))
		{
            json.beginObject();
            for (Map.Entry<String, Option<?>> entry : this.knownFields.entrySet()) {
                json.name(entry.getKey());
                writeOption(entry.getValue(), json);
            }
            json.endObject();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private <T> void writeOption(Option<T> option, JsonWriter json) throws IOException {
        GSON.getAdapter(option.type()).write(json, option.value());
    }
}
