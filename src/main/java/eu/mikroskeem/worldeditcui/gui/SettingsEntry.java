package eu.mikroskeem.worldeditcui.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

/**
 * @author Jesús Sanz - Modified to implement Config GUI / First Version
 */
public class SettingsEntry extends AlwaysSelectedEntryListWidget.Entry<SettingsEntry> {
    private static final int LINE_HEIGHT = 11;

    protected final MinecraftClient client;
    protected final SettingsWidget list;
    protected final Text keyword;
    private List<OrderedText> wrappedKeyword;
    private int lastWidth = -1;
    protected final ClickableWidget widgetButton;
    protected final ClickableWidget resetButton;

    public SettingsEntry(SettingsWidget list, Text keyword, ClickableWidget widgetButton, ClickableWidget resetButton) {
        this.list = list;
        this.client = MinecraftClient.getInstance();
        this.keyword = keyword;
        this.widgetButton = widgetButton;
        this.resetButton = resetButton;
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovering, float delta) {
        // Label
        final int maxNameWidth = rowWidth / 2 - 40;
        if (maxNameWidth != this.lastWidth) {
            this.wrappedKeyword = this.client.textRenderer.wrapLines(this.keyword, maxNameWidth);
            this.lastWidth = maxNameWidth;
        }

        int quarterEntries = this.wrappedKeyword.size() >= 2 ? this.wrappedKeyword.size() / 2 : 0;
        int lineYOffset = (rowHeight / 2) - quarterEntries * (this.client.textRenderer.fontHeight + 2) - this.client.textRenderer.fontHeight / 2;
        for (final OrderedText text : this.wrappedKeyword) {
            this.client.textRenderer.draw(matrices, text, x + 50, y + lineYOffset, 0xFFFFFF);
            lineYOffset += LINE_HEIGHT;
        }

        // Widgets
        this.widgetButton.y = this.resetButton.y = y + 1;
        this.widgetButton.render(matrices, mouseX, mouseY, delta);
        this.resetButton.render(matrices,mouseX, mouseY, delta);
    }

    @Override
    public Text method_37006() {
        return this.keyword;
    }
}
