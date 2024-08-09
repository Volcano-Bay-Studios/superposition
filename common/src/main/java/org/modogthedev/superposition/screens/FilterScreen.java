package org.modogthedev.superposition.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.blockentity.FilterBlockEntity;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionMessages;
import org.modogthedev.superposition.item.FilterItem;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.networking.packet.FilterItemModificationC2SPacket;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class FilterScreen extends WidgetScreen {
    public static final Minecraft mc = Minecraft.getInstance();
    private VertexConsumer lineConsumer;
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Superposition.MODID, "textures/screen/filter_screen.png");
    private static final ResourceLocation BAR = new ResourceLocation(Superposition.MODID, "textures/screen/filter_bar.png");
    private FilterItem.FilterType filterType;
    private float value1;
    private float value2;
    public static int imageWidth = 0;
    public static int imageHeight = 0;
    public static int imageOffset = 0;
    public static int barOffset = 0;
    BlockPos pos;

    protected FilterScreen(Component pTitle, FilterItem.FilterType filterType, float value1, float value2, BlockPos pos) {
        super(pTitle);
        this.filterType = filterType;
        this.value1 = value1;
        this.value2 = value2;
        this.pos = pos;
        switch (filterType) {
            case LOW_PASS -> {
                addDial(-67, 14, 158);
                imageWidth = 176;
                imageHeight = 74;
                imageOffset = 108;
                barOffset = 10;
            }
            case HIGH_PASS -> {
                addDial(68, 15, 158);
                imageWidth = 176;
                imageHeight = 74;
                imageOffset = 182;
                barOffset = 10;
            }
            case BAND_PASS -> {
                addDial(-67, -1, 100);
                addDial(68, 30, 100);
                imageWidth = 176;
                imageHeight = 106;
                imageOffset = 0;
                barOffset = 10;
                dials.get(1).scrolledAmount = value2;
            }
        }
        dials.get(0).scrolledAmount = value1;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = (this.width - imageWidth) / 2;
        int j = (this.height - imageHeight) / 2;
        this.lineConsumer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
        guiGraphics.blit(BACKGROUND, i, j, 0, imageOffset, imageWidth, imageHeight);
        if (pos != null) {
            renderSignals(guiGraphics);
        }
        renderExtra(guiGraphics);
        flush(guiGraphics);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void renderExtra(GuiGraphics guiGraphics) {
        int i = (this.width - imageWidth) / 2;
        int j = (this.height - imageHeight) / 2;
        switch (filterType) {
            case LOW_PASS -> {
                renderBar(guiGraphics, 167, 10, Math.abs((int) dials.get(0).scrolledAmount), true);
                drawFrequencyText(Mth.frequencyToHzReadable(Math.abs(158 - Math.min(158, dials.get(0).scrolledAmount)) * 100000), i + 45, j + 50, 0xFF56d156, guiGraphics);
            }
            case HIGH_PASS -> {
                renderBar(guiGraphics, 9, 10, Math.abs((int) dials.get(0).scrolledAmount), false);
                drawFrequencyText(Mth.frequencyToHzReadable(Math.max(0, dials.get(0).scrolledAmount) * 100000), i + 15, j + 50, 0xFF56d156, guiGraphics);
            }
            case BAND_PASS -> {
                dials.get(0).maxScroll = (int) (158 - dials.get(1).scrolledAmount);
                dials.get(1).maxScroll = (int) (158 - dials.get(0).scrolledAmount);
                renderBar(guiGraphics, 9, 10, Math.abs((int) dials.get(0).scrolledAmount), false);
                renderBar(guiGraphics, 167, 10, Math.abs((int) dials.get(1).scrolledAmount), true);
                drawFrequencyText(Mth.frequencyToHzReadable(Math.max(0, dials.get(0).scrolledAmount) * 100000), i + 45, j + 50, 0xFF56d156, guiGraphics);
                drawFrequencyText(Mth.frequencyToHzReadable(Math.abs(158 - Math.min(158, dials.get(1).scrolledAmount)) * 100000), i + 15, j + 82, 0xFF56d156, guiGraphics);
            }
        }

    }

    private void renderSignals(GuiGraphics guiGraphics) {
        int i = (this.width - imageWidth) / 2;
        int j = (this.height - imageHeight) / 2;
        BlockEntity blockEntity = mc.level.getBlockEntity(pos);
        if (blockEntity instanceof FilterBlockEntity filterBlockEntity) {
            List<Signal> signals = filterBlockEntity.getUnmodulated();
            float highestValue = 0;
            float lowestValue = 0;
            if (signals != null) {
                signals.sort((o1, o2) -> Float.compare(o1.frequency, o2.frequency));
                List<Signal> amplitudeSorted = new ArrayList<>(signals);
                amplitudeSorted.sort((o1, o2) -> Float.compare(o1.amplitude, o2.amplitude));
                if (!amplitudeSorted.isEmpty()) {
                    highestValue = amplitudeSorted.get(amplitudeSorted.size() - 1).amplitude;
                    lowestValue = amplitudeSorted.get(0).amplitude;
                    if (amplitudeSorted.size() == 1)
                        lowestValue = lowestValue / 2;
                }
                for (Signal signal : signals) {
                    float frequency = signal.frequency/100000;
                    float size = (float) (Mth.getFromRange(150,0,11,0,signal.amplitude)+Math.random());
                    if (filterBlockEntity.passCustomValue(frequency,filterType.equals(FilterItem.FilterType.HIGH_PASS) || filterType.equals(FilterItem.FilterType.BAND_PASS) ? dials.get(0).scrolledAmount : 0, (Math.abs(158-(filterType.equals(FilterItem.FilterType.BAND_PASS) ? getValue1() : filterType.equals(FilterItem.FilterType.LOW_PASS) ? dials.get(0).scrolledAmount : 0)))))
                        fillExact(guiGraphics,i+frequency+9,j+barOffset+11-size,i+frequency+10f,j+barOffset+24-11+size,0xFF56d156);
                }
                for (int x = 0; x < 158; x++) {
                    float size = (float) Math.random();
                    if (filterBlockEntity.passCustomValue(x,filterType.equals(FilterItem.FilterType.HIGH_PASS) || filterType.equals(FilterItem.FilterType.BAND_PASS) ? dials.get(0).scrolledAmount : 0, (Math.abs(158-(filterType.equals(FilterItem.FilterType.BAND_PASS) ? getValue1() : filterType.equals(FilterItem.FilterType.LOW_PASS) ? dials.get(0).scrolledAmount : 0)))))
                        fillExact(guiGraphics,i+x+9,j+barOffset+11-size,i+x+10f,j+barOffset+24-11+size,0xFF56d156);
                }
            }
        }
    }
    public float getValue1() {
        if (filterType == FilterItem.FilterType.BAND_PASS) {
            return dials.get(1).scrolledAmount;
        }
        return 0;
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
        if (dials.size() > 1)
            SuperpositionMessages.sendToServer(new FilterItemModificationC2SPacket(dials.get(0).scrolledAmount, dials.get(1).scrolledAmount));
        else
            SuperpositionMessages.sendToServer(new FilterItemModificationC2SPacket(dials.get(0).scrolledAmount, 0));
        super.onClose();
    }
    public void fill(GuiGraphics graphics, float pMinX, float pMinY, float pMaxX, float pMaxY, int pColor) {
        if (pMinY > pMaxY) {
            float minY = pMinY;
            pMinY = pMaxY;
            pMaxY = minY+3;
        } else {
            pMaxY += 3;
        }
        // In ryan we trust
        float f3 = (float) FastColor.ARGB32.alpha(pColor) / 255.0F;
        float f = (float) FastColor.ARGB32.red(pColor) / 255.0F;
        float f1 = (float) FastColor.ARGB32.green(pColor) / 255.0F;
        float f2 = (float) FastColor.ARGB32.blue(pColor) / 255.0F;

        Matrix4f matrix4f = graphics.pose().last().pose();

        this.lineConsumer.vertex(matrix4f, pMinX, pMinY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, pMinX, pMaxY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, pMaxX, pMaxY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, pMaxX, pMinY, 0.0f).color(f, f1, f2, f3).endVertex();
    }
    public void fillExact(GuiGraphics graphics, float pMinX, float pMinY, float pMaxX, float pMaxY, int pColor) {
        // In ryan we trust
        float f3 = (float) FastColor.ARGB32.alpha(pColor) / 255.0F;
        float f = (float) FastColor.ARGB32.red(pColor) / 255.0F;
        float f1 = (float) FastColor.ARGB32.green(pColor) / 255.0F;
        float f2 = (float) FastColor.ARGB32.blue(pColor) / 255.0F;

        Matrix4f matrix4f = graphics.pose().last().pose();

        this.lineConsumer.vertex(matrix4f, pMinX, pMinY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, pMinX, pMaxY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, pMaxX, pMaxY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, pMaxX, pMinY, 0.0f).color(f, f1, f2, f3).endVertex();
    }
    private void flush(GuiGraphics graphics) { // In ryan we trust
        RenderSystem.disableDepthTest();
        graphics.bufferSource().endBatch();
        RenderSystem.enableDepthTest();
        this.lineConsumer = null;
    }
}
