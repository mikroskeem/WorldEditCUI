package eu.mikroskeem.worldeditcui.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

/**
 * @author Jesús Sanz - Modified to implement Config GUI / First Version
 */
public class SettingsEntry extends AlwaysSelectedEntryListWidget.Entry<SettingsEntry> {

    protected final MinecraftClient client;
    protected final SettingsWidget list;
    protected final String keyword;
    protected final AbstractButtonWidget widgetButton;
    protected final AbstractButtonWidget resetButton;

    public SettingsEntry(SettingsWidget list, String keyword, AbstractButtonWidget widgetButton, AbstractButtonWidget resetButton) {
        this.list = list;
        this.client = MinecraftClient.getInstance();
        this.keyword = keyword;
        this.widgetButton = widgetButton;
        this.resetButton = resetButton;
    }

    @Override
    public void render(int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovering, float delta) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        String name = keyword;
        String trimmedName = name;
        int maxNameWidth = rowWidth - 32 - 3;
        if (this.client.textRenderer.getStringWidth(name) > maxNameWidth) {
            trimmedName = this.client.textRenderer.trimToWidth(name, maxNameWidth - this.client.textRenderer.getStringWidth("...")) + "...";
        }
        this.client.textRenderer.draw(trimmedName, x + 70, y + 1, 0xFFFFFF);
        this.widgetButton.y = y + 1;
        this.widgetButton.render(mouseX, mouseY, delta);
        this.resetButton.y = y + 1;
        this.resetButton.render(mouseX, mouseY, delta);
    }

}
