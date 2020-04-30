package eu.mikroskeem.worldeditcui.gui;

import com.mumfrey.worldeditcui.config.CUIConfiguration;
import com.mumfrey.worldeditcui.config.Colour;
import com.mumfrey.worldeditcui.render.ConfiguredColour;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.LogManager;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Mark Vainomaa
 * @author Jesús Sanz - Modified to implement Config GUI / First Version
 */
public class FabricCUIConfigPanel extends Screen implements Supplier<Screen> {

    private final Screen parent;
    private final Set<String> options;
    private final CUIConfiguration configuration;
    private double scrollPercent = 0;
    private SettingsWidget configList;
    private AbstractButtonWidget done;

    private final String screenTitle;

    private static final int BUTTONHEIGHT = 20;

    public FabricCUIConfigPanel(Screen parent, CUIConfiguration configuration) {
        super(new LiteralText("WorldEditCUI"));
        this.parent = parent;
        this.configuration = configuration;
        this.options = this.configuration.getConfigArray().keySet();
        this.screenTitle = I18n.translate("worldeditcui.options.title");
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (configList.isMouseOver(mouseX, mouseY))
            return this.configList.mouseScrolled(mouseX, mouseY, amount);
        return false;
    }

    @Override
    protected void init() {
        configList = new SettingsWidget(this.minecraft, this.width - 8, this.height, 48 + 19, this.height - 36, 25, this.configuration, this);
        configList.setLeftPos(0);
        done = this.addButton(new AbstractButtonWidget(this.width / 2 - 205, this.height - 27, 70, 20, I18n.translate("worldeditcui.options.done")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                for (AbstractButtonWidget button : buttons) {
                    if (button instanceof TextFieldWidget) {
                        if(button.isFocused()) {
                            button.changeFocus(false);
                        }
                    }
                }
                configuration.configChanged();
                assert minecraft != null;
                minecraft.openScreen(parent);
            }
        });

        int y = 0;
        for (String text : options) {
            int buttonX = this.width - 140 - 20;
            Object value = configuration.getConfigArray().get(text);
            AbstractButtonWidget element;
            String textTemp = configuration.getDefaultDisplayName(text);
            if (value == null) {
                LogManager.getLogger().warn("value null, adding nothing");
                continue;
            } else if(value instanceof Boolean) {
                element = this.addButton(new AbstractButtonWidget(buttonX, y, 70, BUTTONHEIGHT, ((Boolean) value ? "§2true" : "§4false")) {
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
                        this.setMessage((Boolean) configuration.getConfigArray().get(text) ? "§2true" : "§4false");
                        super.onFocusedChanged(bl);
                    }
                });
            } else if(value instanceof Colour) {
                element = this.addButton(new TextFieldWidgetTemp(font, buttonX, y, 70, BUTTONHEIGHT, ((Colour)value).getHex(), ((Colour)value).getHex()) {
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
                LogManager.getLogger().warn("WorldEditCUI has option "+text+" with data type "+value.getClass().getName());
                continue;
            }
            this.configList.addEntry(new SettingsEntry(this.configList, (textTemp != null) ? textTemp : text, element, this.addButton(new AbstractButtonWidget(buttonX + 75, y, BUTTONHEIGHT, BUTTONHEIGHT, "") {
                @Override
                public void onClick(double mouseX, double mouseY) {
                    configuration.changeValue(text, configuration.getDefaultValue(text));
                    element.changeFocus(false);
                }
            })));
        }

        this.setInitialFocus(null);
        this.children.add(this.configList);
    }

    @Override
    public Screen get() {
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        super.render(mouseX, mouseY, delta);
        this.configList.render(mouseX, mouseY, delta);
        drawCenteredString(font, screenTitle, this.configList.getWidth() / 2, BUTTONHEIGHT, 0xFFFFFF);
        this.done.render(mouseX, mouseY, delta);
    }

    double getScrollPercent() {
        return scrollPercent;
    }

    void updateScrollPercent(double scrollPercent) {
        this.scrollPercent = scrollPercent;
    }

    class TextFieldWidgetTemp extends TextFieldWidget {

        public TextFieldWidgetTemp(TextRenderer textRenderer, int x, int y, int width, int height, String message, String text) {
            super(textRenderer, x, y, width, height, message);
            this.setText(text);
        }
    }
}
