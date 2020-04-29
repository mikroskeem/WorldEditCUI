package eu.mikroskeem.worldeditcui.gui;

import com.mumfrey.worldeditcui.config.CUIConfiguration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

/**
 * @author Jesús Sanz - Modified to implement Config GUI / First Version
 */
public class SettingsWidget extends AlwaysSelectedEntryListWidget<SettingsEntry> {

    private final FabricCUIConfigPanel parent;
    private boolean scrolling;
    private CUIConfiguration configuration;

    public SettingsWidget(MinecraftClient client, int width, int height, int y1, int y2, int entryHeight, CUIConfiguration configuration, FabricCUIConfigPanel parent) {
        super(client, width, height, y1, y2, entryHeight);
        this.configuration = configuration;
        this.parent = parent;
        setScrollAmount(parent.getScrollPercent() * Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)));
    }

    @Override
    public void setScrollAmount(double amount) {
        super.setScrollAmount(amount);
        int denominator = Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
        if(denominator <= 0) {
            parent.updateScrollPercent(0);
        } else {
            parent.updateScrollPercent(getScrollAmount() / Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)));
        }
    }

    @Override
    protected boolean isFocused() {
        return parent.getFocused() == this;
    }

    @Override
    protected int addEntry(SettingsEntry entry) {
        return super.addEntry(entry);
    }

    @Override
    protected void renderList(int x, int y, int mouseX, int mouseY, float delta) {
        int itemCount = this.getItemCount();

        for (int index = 0; index < itemCount; ++index) {
            int entryTop = this.getRowTop(index) + 2;
            int entryBottom = this.getRowTop(index) + this.itemHeight;
            if (entryBottom >= this.top && entryTop <= this.bottom) {
                int entryHeight = this.itemHeight - 4;
                SettingsEntry entry = this.getEntry(index);
                int rowWidth = this.getRowWidth();
                int entryLeft = this.getRowLeft();
                entry.render(index, entryTop, entryLeft, rowWidth, entryHeight, mouseX, mouseY, this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPos(mouseX, mouseY), entry), delta);
            }
        }
    }

    public final SettingsEntry getEntryAtPos(double x, double y) {
        int int_5 = MathHelper.floor(y - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int index = int_5 / this.itemHeight;
        return x < (double) this.getScrollbarPosition() && x >= (double) getRowLeft() && x <= (double) (getRowLeft() + getRowWidth()) && index >= 0 && int_5 >= 0 && index < this.getItemCount() ? this.children().get(index) : null;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6;
    }

    @Override
    public int getRowWidth() {
        return this.width - (Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)) > 0 ? 18 : 12);
    }

    @Override
    protected int getRowLeft() {
        return left + 6;
    }

    public int getWidth() {
        return width;
    }

    @Override
    protected int getMaxPosition() {
        return super.getMaxPosition() + 4;
    }

    @Override
    protected void updateScrollingState(double double_1, double double_2, int int_1) {
        super.updateScrollingState(double_1, double_2, int_1);
        this.scrolling = int_1 == 0 && double_1 >= (double) this.getScrollbarPosition() && double_1 < (double) (this.getScrollbarPosition() + 6);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            SettingsEntry entry = this.getEntryAtPos(mouseX, mouseY);
            if (entry != null) {
                if (entry.mouseClicked(mouseX, mouseY, button)) {
                    this.setFocused(entry);
                    this.setDragging(true);
                    return true;
                }
            } else if (button == 0) {
                this.clickedHeader((int) (mouseX - (double) (this.left + this.width / 2 - this.getRowWidth() / 2)), (int) (mouseY - (double) this.top) + (int) this.getScrollAmount() - 4);
                return true;
            }

            return this.scrolling;
        }
    }
}
