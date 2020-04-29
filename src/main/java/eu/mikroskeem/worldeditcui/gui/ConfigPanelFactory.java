package eu.mikroskeem.worldeditcui.gui;

import com.google.common.collect.ImmutableMap;
import eu.mikroskeem.worldeditcui.FabricModWorldEditCUI;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Mark Vainomaa
 */
public final class ConfigPanelFactory implements ModMenuApi {
    @Override
    public String getModId() {
        return "worldeditcui";
    }

    @Override
    public Optional<Supplier<Screen>> getConfigScreen(Screen screen) {
        return Optional.of(new FabricCUIConfigPanel(screen, FabricModWorldEditCUI.getInstance().getController().getConfiguration()));
    }
}
