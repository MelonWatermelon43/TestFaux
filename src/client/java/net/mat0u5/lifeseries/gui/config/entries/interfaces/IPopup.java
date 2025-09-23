package net.mat0u5.lifeseries.gui.config.entries.interfaces;

import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.DrawContext;

public interface IPopup {
    boolean shouldShowPopup();
    int getPopupWidth();
    int getPopupHeight();
    void renderContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta);

    default int getActualPopupWidth() {
        int width = getPopupWidth() + getPadding();
        if (width % 2 != 0) width++;
        return width;
    }

    default int getActualPopupHeight() {
        return getPopupHeight() + getPadding();
    }

    default int getPadding() {
        return 4;
    }

    default void renderPopup(DrawContext context, int x, int y, int mouseX, int mouseY, float tickDelta) {
        if (!shouldShowPopup()) return;
        //? if <= 1.21.5 {
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 100);
        //?} else {
        /*context.getMatrices().pushMatrix();
        *///?}
        int width = getActualPopupWidth();
        int height = getActualPopupHeight();
        renderBackground(context, x, y, width, height, mouseX, mouseY, tickDelta);
        renderContent(context, x+getPadding()/2, y+getPadding()/2, width, height, mouseX, mouseY, tickDelta);
        //? if <= 1.21.5 {
        context.getMatrices().pop();
        //?} else {
        /*context.getMatrices().popMatrix();
        *///?}
    }

    default void renderBackground(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta) {
        context.fill(x, y, x + width, y + height, TextColors.LIGHT_BLACK);
        RenderUtils.drawBorder(context, x, y, width, height, TextColors.LIGHT_GRAY);
    }
}