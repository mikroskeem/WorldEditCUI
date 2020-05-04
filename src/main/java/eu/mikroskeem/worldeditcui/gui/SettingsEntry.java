package eu.mikroskeem.worldeditcui.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

import java.util.StringTokenizer;

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
        int maxNameWidth = rowWidth / 2 - 40;
        StringTokenizer tok = new StringTokenizer(name, " ");
        StringBuilder outputName = new StringBuilder(name.length());
        int lineLength = 0;
        while(tok.hasMoreElements()) {
            String word = tok.nextToken();

            if(lineLength + this.client.textRenderer.getStringWidth(word) + this.client.textRenderer.getStringWidth(" ") > maxNameWidth) {
                outputName.append("\n");
                lineLength = 0;
            }
            outputName.append(word).append(" ");
            lineLength += this.client.textRenderer.getStringWidth(word);
        }
        tok = new StringTokenizer(outputName.toString(), "\n");
        int quarterEntries = tok.countTokens() >= 2 ? tok.countTokens() / 2 : 0;
        lineLength = (rowHeight / 2) - quarterEntries * (this.client.textRenderer.fontHeight + 2) - this.client.textRenderer.fontHeight / 2;
        while(tok.hasMoreElements()) {
            this.client.textRenderer.draw(tok.nextToken(), x + 50, y + lineLength, 0xFFFFFF);
            lineLength += 10;
        }
        this.widgetButton.y = this.resetButton.y = y + 1;
        this.widgetButton.render(mouseX, mouseY, delta);
        this.resetButton.render(mouseX, mouseY, delta);
    }

}
