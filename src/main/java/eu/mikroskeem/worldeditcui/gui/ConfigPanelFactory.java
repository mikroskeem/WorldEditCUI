package eu.mikroskeem.worldeditcui.gui;

import com.mumfrey.worldeditcui.config.ColourOption;
import com.mumfrey.worldeditcui.config.FlagOption;
import eu.mikroskeem.worldeditcui.FabricModWorldEditCUI;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.ColorEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static com.mumfrey.worldeditcui.WorldEditCUI.tr;

/**
 * The builder for the configuration screen for WorldEditCUI.
 *
 * @author Mark Vainomaa
 */
public final class ConfigPanelFactory implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::makeConfigScreen;
    }

    private Screen makeConfigScreen(final Screen parent) {
        final ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(tr("options.title"))
            .setTransparentBackground(MinecraftClient.getInstance().world != null);

        builder.setSavingRunnable(() -> {
            FabricModWorldEditCUI.getInstance().getController().getConfiguration().save();
        });

        final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        final ConfigCategory main = builder.getOrCreateCategory(tr("options.category.main"));


        final SubCategoryBuilder colours = entryBuilder.startSubCategory(tr("options.category.main.colours"));
        for (ColourOption colour : ColourOption.values()) {
            colours.add(buildEntry(builder.entryBuilder(), colour));
        }
        colours.setExpanded(true);
        main.addEntry(colours.build());

        final SubCategoryBuilder flags = entryBuilder.startSubCategory(tr("options.category.main.flags"));
        for (FlagOption flag : FlagOption.values()) {
            flags.add(buildEntry(builder.entryBuilder(), flag));
        }
        main.addEntry(flags.build());

        return builder.build();
    }

    private ColorEntry buildEntry(final ConfigEntryBuilder builder, final ColourOption option) {
        return builder.startColorField(option.label(), option.getColourIntARGB())
            .setDefaultValue(() -> option.defaultValue().getIntARGB())
            .setAlphaMode(true)
            .setSaveConsumer(option::setColourIntRGBA)
            .build();
    }

    private BooleanListEntry buildEntry(final ConfigEntryBuilder builder, final FlagOption option) {
        return builder.startBooleanToggle(option.label(), option.value())
            .setDefaultValue(option.defaultValue())
            .setSaveConsumer(value -> {
            })
            .build();
    }
}
