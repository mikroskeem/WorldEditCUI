package eu.mikroskeem.worldeditcui.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import eu.mikroskeem.worldeditcui.FabricModWorldEditCUI;

/**
 * @author Mark Vainomaa
 */
public final class ConfigPanelFactory implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new FabricCUIConfigPanel(parent, FabricModWorldEditCUI.getInstance().getController().getConfiguration());
    }
}
