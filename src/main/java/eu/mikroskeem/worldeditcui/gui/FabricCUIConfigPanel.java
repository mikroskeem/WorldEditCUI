package eu.mikroskeem.worldeditcui.gui;

import com.mumfrey.worldeditcui.config.CUIConfiguration;
import com.mumfrey.worldeditcui.config.Colour;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Mark Vainomaa
 * @author Jesús Sanz - Modified to implement Config GUI / First Version
 */
public class FabricCUIConfigPanel extends Screen implements Supplier<Screen> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Text TRUE = new LiteralText("true").styled(s -> s.withFormatting(Formatting.DARK_GREEN));
    private static final Text FALSE = new LiteralText("false").styled(s -> s.withFormatting(Formatting.DARK_RED));

    private final Screen parent;
    private final Set<String> options;
    private final CUIConfiguration configuration;
    private double scrollPercent = 0;
    private SettingsWidget configList;
    private ClickableWidget done;

    private final Text screenTitle;

    private static final int BUTTONHEIGHT = 20;

    public FabricCUIConfigPanel(Screen parent, CUIConfiguration configuration) {
        super(new LiteralText("WorldEditCUI"));
        this.parent = parent;
        this.configuration = configuration;
        this.options = this.configuration.getConfigArray().keySet();
        this.screenTitle = new TranslatableText("worldeditcui.options.title");
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (configList.isMouseOver(mouseX, mouseY))
            return this.configList.mouseScrolled(mouseX, mouseY, amount);
        return false;
    }

    @Override
    protected void init() {
        configList = new SettingsWidget(this.client, this.width - 8, this.height, 48 + 19, this.height - 36, 25, this.configuration, this);
        configList.setLeftPos(0);
        done = this.addDrawableChild(new ClickableWidget(this.width / 2 - 205, this.height - 27, 70, 20, new TranslatableText("worldeditcui.options.done")) {
            @Override
            public void appendNarrations(NarrationMessageBuilder builder) {
                this.method_37021(builder);
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                for (Element button : children()) {
                    if (button instanceof TextFieldWidget widget) {
                        if(widget.isFocused()) {
                            widget.changeFocus(false);
                        }
                    }
                }
                configuration.configChanged();
                assert client != null;
                client.openScreen(parent);
            }
        });

        int y = 0;
        for (String text : options) {
            int buttonX = this.width - 140 - 20;
            Object value = configuration.getConfigArray().get(text);
            ClickableWidget element;
            TranslatableText textTemp = configuration.getDescription(text);
            if (value == null) {
                LOGGER.warn("value null, adding nothing");
                continue;
            } else if(value instanceof Boolean) {
                element = this.addDrawableChild(new ClickableWidget(buttonX, y, 70, BUTTONHEIGHT, ((Boolean) value ? TRUE: FALSE)) {
                    @Override
                    public void appendNarrations(NarrationMessageBuilder builder) {
                        this.method_37021(builder);
                    }

                    @Override
                    public void onClick(double mouseX, double mouseY) {
                        if ((Boolean) (configuration.getConfigArray().get(text))) {
                            configuration.changeValue(text, false);
                        } else {
                            configuration.changeValue(text, true);
                        }
                        this.changeFocus(true);
                    }

                    @Override
                    protected void onFocusedChanged(boolean bl) {
                        this.setMessage((Boolean) configuration.getConfigArray().get(text) ? TRUE : FALSE);
                        super.onFocusedChanged(bl);
                    }
                });
            } else if(value instanceof Colour) {
                element = this.addDrawableChild(new TextFieldWidgetTemp(this.textRenderer, buttonX, y, 70, BUTTONHEIGHT, new LiteralText(((Colour)value).getHex()), ((Colour)value).getHex()) {
                    @Override
                    protected void onFocusedChanged(boolean bl) {
                        if (bl) {
                            this.setText(((Colour)configuration.getConfigArray().get(text)).getHex());
                        } else {
                            configuration.changeValue(text, new Colour(this.getText()));
                        }
                        super.onFocusedChanged(bl);
                    }

                    @Override
                    public boolean charTyped(char chr, int keyCode) {
                        boolean result = super.charTyped(chr, keyCode);
                        configuration.changeValue(text, new Colour(this.getText()));
                        return result;
                    }

                    @Override
                    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
                        configuration.changeValue(text, new Colour(this.getText()));
                        return result;
                    }
                });
            } else {
                LOGGER.warn("WorldEditCUI has option " + text + " with data type " + value.getClass().getName());
                continue;
            }
            this.configList.addEntry(new SettingsEntry(this.configList, (textTemp != null) ? textTemp : new LiteralText(text), element, this.addDrawableChild(new ClickableWidget(buttonX + 75, y, BUTTONHEIGHT, BUTTONHEIGHT, LiteralText.EMPTY) {
                @Override
                public void appendNarrations(NarrationMessageBuilder builder) {
                    this.method_37021(builder);
                }

                @Override
                public void onClick(double mouseX, double mouseY) {
                    configuration.changeValue(text, configuration.getDefaultValue(text));
                    element.changeFocus(false);
                }
            })));
        }
        this.setFocused(null);
        this.addDrawable(this.configList);
    }

    @Override
    public Screen get() {
        return this;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.configList.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, screenTitle, this.configList.getWidth() / 2, BUTTONHEIGHT, 0xFFFFFF);
        this.done.render(matrices, mouseX, mouseY, delta);
    }

    double getScrollPercent() {
        return scrollPercent;
    }

    void updateScrollPercent(double scrollPercent) {
        this.scrollPercent = scrollPercent;
    }

    static class TextFieldWidgetTemp extends TextFieldWidget {

        public TextFieldWidgetTemp(TextRenderer textRenderer, int x, int y, int width, int height, Text message, String text) {
            super(textRenderer, x, y, width, height, message);
            this.setText(text);
        }
    }
}
