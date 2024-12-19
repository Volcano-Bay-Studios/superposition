package org.modogthedev.superposition.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.blockentity.FilterBlockEntity;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.networking.packet.FilterItemModificationC2SPacket;
import org.modogthedev.superposition.system.filter.BandPassFilter;
import org.modogthedev.superposition.system.filter.Filter;
import org.modogthedev.superposition.system.filter.HighPassFilter;
import org.modogthedev.superposition.system.filter.LowPassFilter;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FilterScreen extends WidgetScreen {
    public static final Minecraft mc = Minecraft.getInstance();
    private VertexConsumer lineConsumer;
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/filter_screen.png");
    private static final ResourceLocation BAR = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/filter_bar.png");
    private Filter filterType;
    private float value1;
    private float value2;
    public static int imageWidth = 0;
    public static int imageHeight = 0;
    public static int imageOffset = 0;
    public static int barOffset = 0;
    private boolean editMode;
    private BlockPos pos;

    protected FilterScreen(Component pTitle, Filter filter, BlockPos pos, boolean editMode) {
        super(pTitle);
        this.editMode = editMode;
        this.filterType = filter;
        this.pos = pos;
        if (filter instanceof LowPassFilter) {
            addDial(-67, 14, 158);
            imageWidth = 176;
            imageHeight = 74;
            imageOffset = 108;
            barOffset = 10;
        }
        if (filter instanceof HighPassFilter) {
            addDial(68, 15, 158);
            imageWidth = 176;
            imageHeight = 74;
            imageOffset = 182;
            barOffset = 10;
        }
        if (filter instanceof BandPassFilter) {
            addDial(-67, -1, 158);
            addDial(68, 30, 158);
            imageWidth = 176;
            imageHeight = 106;
            imageOffset = 0;
            barOffset = 10;
        }
        filter.updateDials(dials);
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
        if (filterType instanceof LowPassFilter) {
            renderBar(guiGraphics, 167, 10, Math.abs((int) dials.get(0).scrolledAmount), true);
            drawFrequencyText(SuperpositionMth.frequencyToHzReadable(Math.abs(158 - Math.min(158, dials.get(0).scrolledAmount)) * 100000), i + 45, j + 50, 0xFF56d156, guiGraphics);

        }
        if (filterType instanceof HighPassFilter) {
            renderBar(guiGraphics, 9, 10, Math.abs((int) dials.get(0).scrolledAmount), false);
            drawFrequencyText(SuperpositionMth.frequencyToHzReadable(Math.max(0, dials.get(0).scrolledAmount) * 100000), i + 15, j + 50, 0xFF56d156, guiGraphics);
        }
        if (filterType instanceof BandPassFilter) {
            dials.get(0).maxScroll = (int) (158 - dials.get(1).scrolledAmount);
            dials.get(1).maxScroll = (int) (158 - dials.get(0).scrolledAmount);
            renderBar(guiGraphics, 9, 10, Math.abs((int) dials.get(0).scrolledAmount), false);
            renderBar(guiGraphics, 167, 10, Math.abs((int) dials.get(1).scrolledAmount), true);
            drawFrequencyText(SuperpositionMth.frequencyToHzReadable(Math.max(0, dials.get(0).scrolledAmount) * 100000), i + 45, j + 50, 0xFF56d156, guiGraphics);
            drawFrequencyText(SuperpositionMth.frequencyToHzReadable(Math.abs(158 - Math.min(158, dials.get(1).scrolledAmount)) * 100000), i + 15, j + 82, 0xFF56d156, guiGraphics);

        }
    }

    private void renderSignals(GuiGraphics guiGraphics) {
        int i = (this.width - imageWidth) / 2;
        int j = (this.height - imageHeight) / 2;
        BlockEntity blockEntity = mc.level.getBlockEntity(pos);
        List<Signal> signals = null;
        if (blockEntity instanceof FilterBlockEntity filterBlockEntity) {
            signals = filterBlockEntity.getUnmodulated();
        } else if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity) {
            signals = signalActorBlockEntity.getSignals();
        }
        float highestValue = 0;
        float lowestValue = 0;
        if (signals != null) {
            signals.sort(Comparator.comparingDouble(Signal::getFrequency));
            List<Signal> amplitudeSorted = new ArrayList<>(signals);
            amplitudeSorted.sort((o1, o2) -> Float.compare(o1.getAmplitude(), o2.getAmplitude()));
            if (!amplitudeSorted.isEmpty()) {
                highestValue = amplitudeSorted.getLast().getAmplitude();
                lowestValue = amplitudeSorted.getFirst().getAmplitude();
                if (amplitudeSorted.size() == 1)
                    lowestValue = lowestValue / 2;
            }
            for (Signal signal : signals) {
                float frequency = signal.getFrequency() / 100000;
                float size = (float) (SuperpositionMth.getFromRange(150, 0, 11, 0, signal.getAmplitude()) + Math.random());
                fillExact(guiGraphics, i + frequency + 9, j + barOffset + 11 - size, i + frequency + 10f, j + barOffset + 24 - 11 + size, 0xFF56d156);
            }
            for (int x = 0; x < 158; x++) {
                float size = (float) Math.random();
                fillExact(guiGraphics, i + x + 9, j + barOffset + 11 - size, i + x + 10f, j + barOffset + 24 - 11 + size, 0xFF56d156);
            }
        }
    }

    public float getValue1() {
        if (filterType instanceof BandPassFilter) {
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
        if (editMode) {
            updateBlock();
            super.onClose();
        } else {
            filterType.updateFromDials(dials);
            VeilPacketManager.server().sendPacket(new FilterItemModificationC2SPacket(filterType));
            super.onClose();
        }
    }

    public void updateBlock() {
        CompoundTag tag = new CompoundTag();
        if (filterType != null) {
            filterType.updateFromDials(dials);
            filterType.save(tag);
            tag.putString("namespace", filterType.getSelfReference().getNamespace());
            tag.putString("path", filterType.getSelfReference().getPath());
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, pos));
        }
    }


    public void fill(GuiGraphics graphics, float pMinX, float pMinY, float pMaxX, float pMaxY, int pColor) {
        if (pMinY > pMaxY) {
            float minY = pMinY;
            pMinY = pMaxY;
            pMaxY = minY + 3;
        } else {
            pMaxY += 3;
        }
        // In ryan we trust
        float f3 = (float) FastColor.ARGB32.alpha(pColor) / 255.0F;
        float f = (float) FastColor.ARGB32.red(pColor) / 255.0F;
        float f1 = (float) FastColor.ARGB32.green(pColor) / 255.0F;
        float f2 = (float) FastColor.ARGB32.blue(pColor) / 255.0F;

        Matrix4f matrix4f = graphics.pose().last().pose();

        this.lineConsumer.addVertex(matrix4f, pMinX, pMinY, 0.0f).setColor(f, f1, f2, f3);
        this.lineConsumer.addVertex(matrix4f, pMinX, pMaxY, 0.0f).setColor(f, f1, f2, f3);
        this.lineConsumer.addVertex(matrix4f, pMaxX, pMaxY, 0.0f).setColor(f, f1, f2, f3);
        this.lineConsumer.addVertex(matrix4f, pMaxX, pMinY, 0.0f).setColor(f, f1, f2, f3);
    }

    public void fillExact(GuiGraphics graphics, float pMinX, float pMinY, float pMaxX, float pMaxY, int pColor) {
        // In ryan we trust
        float f3 = (float) FastColor.ARGB32.alpha(pColor) / 255.0F;
        float f = (float) FastColor.ARGB32.red(pColor) / 255.0F;
        float f1 = (float) FastColor.ARGB32.green(pColor) / 255.0F;
        float f2 = (float) FastColor.ARGB32.blue(pColor) / 255.0F;

        Matrix4f matrix4f = graphics.pose().last().pose();

        this.lineConsumer.addVertex(matrix4f, pMinX, pMinY, 0.0f).setColor(f, f1, f2, f3);
        this.lineConsumer.addVertex(matrix4f, pMinX, pMaxY, 0.0f).setColor(f, f1, f2, f3);
        this.lineConsumer.addVertex(matrix4f, pMaxX, pMaxY, 0.0f).setColor(f, f1, f2, f3);
        this.lineConsumer.addVertex(matrix4f, pMaxX, pMinY, 0.0f).setColor(f, f1, f2, f3);
    }

    private void flush(GuiGraphics graphics) { // In ryan we trust
        RenderSystem.disableDepthTest();
        graphics.bufferSource().endBatch();
        RenderSystem.enableDepthTest();
        this.lineConsumer = null;
    }
}
