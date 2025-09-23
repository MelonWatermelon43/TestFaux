package net.mat0u5.lifeseries.gui.config.entries.main;

import net.mat0u5.lifeseries.gui.config.entries.EmptyConfigEntry;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.interfaces.IEntryGroupHeader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
//? if >= 1.21.9
/*import net.minecraft.client.gui.Click;*/

public class TextConfigEntry extends EmptyConfigEntry implements IEntryGroupHeader {
    protected static final int TEXT_OFFSET_X = -5;
    protected static final int TEXT_OFFSET_Y = LABEL_OFFSET_Y;

    private final boolean clickable;
    public boolean clicked;

    public TextConfigEntry(String fieldName, String displayName, String description) {
        this(fieldName, displayName, description, true);
    }

    public TextConfigEntry(String fieldName, String displayName, String description, boolean clickable) {
        super(fieldName, displayName, description);
        this.clickable = clickable;
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (!clickable) return;
        String text = !clicked ? "Click to expand" : "Click to collapse";
        RenderUtils.drawTextRight(context, textRenderer, TextColors.LIGHT_GRAY_A128, Text.of(text), x + width + TEXT_OFFSET_X, y + TEXT_OFFSET_Y);
    }

    @Override
    //? if <= 1.21.6 {
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        if (clickable && button == 0) {
    //?} else {
    /*protected boolean mouseClickedEntry(Click click, boolean doubled) {
    if (clickable && click.isLeft()) {
    *///?}
            clicked = !clicked;
        }
        return clickable;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.TEXT;
    }

    @Override
    public void expand() {
        clicked = true;
    }

    @Override
    public boolean shouldExpand() {
        return clicked;
    }
}