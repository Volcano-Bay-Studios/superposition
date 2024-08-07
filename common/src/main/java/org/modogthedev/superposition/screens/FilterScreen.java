package org.modogthedev.superposition.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionMessages;
import org.modogthedev.superposition.item.FilterItem;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.networking.packet.FilterItemModificationC2SPacket;
import org.modogthedev.superposition.util.Mth;

public class FilterScreen extends WidgetScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Superposition.MODID, "textures/screen/filter_screen.png");
    private static final ResourceLocation BAR = new ResourceLocation(Superposition.MODID, "textures/screen/filter_bar.png");
    private FilterItem.FilterType filterType;
    private float value1;
    private float value2;
    public static int imageWidth = 0;
    public static int imageHeight = 0;
    public static int imageOffset = 0;

    protected FilterScreen(Component pTitle, FilterItem.FilterType filterType,float value1,float value2) {
        super(pTitle);
        this.value1 = value1;
        this.value2 = value2;
        this.filterType = filterType;
        switch (filterType) {
            case LOW_PASS -> {
                addDial(-67, 14, 158);
                imageWidth = 176;
                imageHeight = 74;
                imageOffset = 108;
            }
            case HIGH_PASS -> {
                addDial(68, 15, 158);
                imageWidth = 176;
                imageHeight = 74;
                imageOffset = 182;
            }
            case BAND_PASS -> {
                addDial(-67, -1, 100);
                addDial(68, 30, 100);
                imageWidth = 176;
                imageHeight = 106;
                imageOffset = 0;
                dials.get(1).scrolledAmount = value2;
            }
        }
        dials.get(0).scrolledAmount = value1;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = (this.width - imageWidth) / 2;
        int j = (this.height - imageHeight) / 2;
        guiGraphics.blit(BACKGROUND, i, j, 0, imageOffset, imageWidth, imageHeight);
        renderExtra(guiGraphics);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void renderExtra(GuiGraphics guiGraphics) {
        int i = (this.width - imageWidth) / 2;
        int j = (this.height - imageHeight) / 2;
        switch (filterType) {
            case LOW_PASS -> {
                renderBar(guiGraphics, 167, 10, Math.abs((int) dials.get(0).scrolledAmount), true);
                drawFrequencyText(Mth.frequencyToHzReadable(Math.abs(158-Math.min(158,dials.get(0).scrolledAmount))*100000),i+45,j+50,0xFF56d156,guiGraphics);
            }
            case HIGH_PASS -> {
                renderBar(guiGraphics, 9, 10, Math.abs((int) dials.get(0).scrolledAmount), false);
                drawFrequencyText(Mth.frequencyToHzReadable(Math.max(0,dials.get(0).scrolledAmount)*100000),i+15,j+50,0xFF56d156,guiGraphics);
            }
            case BAND_PASS -> {
                dials.get(0).maxScroll = (int) (158-dials.get(1).scrolledAmount);
                dials.get(1).maxScroll = (int) (158-dials.get(0).scrolledAmount);
                renderBar(guiGraphics, 9, 10, Math.abs((int) dials.get(0).scrolledAmount), false);
                renderBar(guiGraphics, 167, 10, Math.abs((int) dials.get(1).scrolledAmount), true);
                drawFrequencyText(Mth.frequencyToHzReadable(Math.max(0,dials.get(0).scrolledAmount)*100000),i+45,j+50,0xFF56d156,guiGraphics);
                drawFrequencyText(Mth.frequencyToHzReadable(Math.abs(158-Math.min(158,dials.get(1).scrolledAmount))*100000),i+15,j+82,0xFF56d156,guiGraphics);
            }
        }
    }

    private void renderBar(GuiGraphics guiGraphics, int startX, int startY, int width, boolean invert) {
        int i = (this.width - imageWidth) / 2;
        int j = (this.height - imageHeight) / 2;
        int x = 0;
        if (!invert)
            for (int w = 0; w < (width + 6); w += 6) {
                guiGraphics.blit(BACKGROUND, i + startX + w, j + startY, 250, 0, Math.min(width - w, 6), 24);
                if (w > 1000) {
                    break;
                }
                x = w;
            }
        else
            for (int w = width; w > (0); w -= 6) {
                guiGraphics.blit(BACKGROUND, i + startX - w, j + startY, 250, 24, Math.min(w, 6), 24);
                if (w > 1000) {
                    break;
                }
                x = w;
            }
    }
    private void drawFrequencyText(String text, int x, int y, int color, GuiGraphics guiGraphics) {
        guiGraphics.drawString(this.font, Component.literal(text), x, y, color);
    }

    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if (dials.size()>1)
            SuperpositionMessages.sendToServer(new FilterItemModificationC2SPacket(dials.get(0).scrolledAmount,dials.get(1).scrolledAmount));
        else
            SuperpositionMessages.sendToServer(new FilterItemModificationC2SPacket(dials.get(0).scrolledAmount,0));
        super.onClose();
    }
}
