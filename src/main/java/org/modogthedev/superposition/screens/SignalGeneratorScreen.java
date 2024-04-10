package org.modogthedev.superposition.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.SignalGeneratorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionSounds;
import org.modogthedev.superposition.event.ClientEvents;
import org.modogthedev.superposition.networking.Messages;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.SyncedBlockEntity;

import java.awt.*;

public class SignalGeneratorScreen extends DialScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Superposition.MODID, "textures/screen/signal_generator_background.png");
    private static final ResourceLocation PIXEL = new ResourceLocation(Superposition.MODID, "textures/screen/pixel.png");
    private static final ResourceLocation WARN_ON = new ResourceLocation(Superposition.MODID, "textures/screen/warn_on.png");
    private static final ResourceLocation WARN_OFF = new ResourceLocation(Superposition.MODID, "textures/screen/warn_off.png");
    private static final ResourceLocation SWITCH_ON = new ResourceLocation(Superposition.MODID, "textures/screen/switch_on.png");
    private static final ResourceLocation SWITCH_OFF = new ResourceLocation(Superposition.MODID, "textures/screen/switch_off.png");
    public static final int imageWidth = 176;
    public static final int imageHeight = 224;
    public static BlockPos pos;
    public static int ticks = 0;
    public float frequency;
    public float startFrequency = 1;
    public boolean mute = true;
    public boolean swap = false;
    public VertexConsumer lineConsumer;

    public SignalGeneratorScreen(Component pTitle, BlockPos pos) {
        super(pTitle);
        SignalGeneratorScreen.pos = pos;
        ticks = 0;
        addDial(-25, 0);
        addDial(25, 0);
        BlockState state = Minecraft.getInstance().level.getBlockState(pos);
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof SignalGeneratorBlockEntity generatorBlockEntity) {
            startFrequency = generatorBlockEntity.frequency;
        }
        swap = state.getValue(SignalGeneratorBlock.SWAP_SIDES);
    }

    public void playDownSound(SoundManager pHandler) {
        pHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void drawPixel(GuiGraphics pGuiGraphics, int x, int y) {

        pGuiGraphics.blit(PIXEL, x, y, 0, 0, 1, 1);
    }

    public void fill(GuiGraphics graphics, int pMinX, int pMinY, int pMaxX, int pMaxY, int pColor) { // In ryan we trust
        float f3 = (float) FastColor.ARGB32.alpha(pColor) / 255.0F;
        float f = (float) FastColor.ARGB32.red(pColor) / 255.0F;
        float f1 = (float) FastColor.ARGB32.green(pColor) / 255.0F;
        float f2 = (float) FastColor.ARGB32.blue(pColor) / 255.0F;

        Matrix4f matrix4f = graphics.pose().last().pose();

        this.lineConsumer.vertex(matrix4f, (float) pMinX, (float) pMinY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, (float) pMinX, (float) pMaxY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, (float) pMaxX, (float) pMaxY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, (float) pMaxX, (float) pMinY, 0.0f).color(f, f1, f2, f3).endVertex();
    }

    public void renderSine(GuiGraphics pGuiGraphics) {
        this.lineConsumer = pGuiGraphics.bufferSource().getBuffer(RenderType.gui()); // In ryan we trust
        int startPos = (this.width - 158) / 2;
        int j = (this.height - imageHeight) / 2;
        int width = this.width;
        for (float i = 0; i < 158; i += .05f) {
            int calculatedPosition = (int) (Math.sin((double) (i + ticks) / frequency) * 25);
            fill(pGuiGraphics, (int) (i + (startPos)), (j + 45 + calculatedPosition), (int) (i + (startPos)) + 1, (j + 45 + calculatedPosition) + 1, 0xFF56d156);
        }
        flush(pGuiGraphics);
        if (frequency < .72f) {
            pGuiGraphics.blit(WARN_ON, width / 2 - 81, height / 2 - 20, 0, 0, 14, 14, 14, 14);
        } else {
            pGuiGraphics.blit(WARN_OFF, width / 2 - 81, height / 2 - 20, 0, 0, 14, 14, 14, 14);
        }
    }

    public void calculateWavelength() {
        frequency = Math.abs((dials.get(0).scrolledAmount / 10) + (dials.get(1).scrolledAmount)+(startFrequency));
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if ((double) width / 2 + 72 > pMouseX - 10 && (double) width / 2 + 72 < pMouseX && (double) height / 2 - 20 > pMouseY - 24 && (double) height / 2 - 20 < pMouseY) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            mute = !mute;
        }
        if ((double) width / 2 + 58 > pMouseX - 10 && (double) width / 2 + 60 < pMouseX && (double) height / 2 - 20 > pMouseY - 24 && (double) height / 2 - 20 < pMouseY) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            swap = !swap;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    private void flush(GuiGraphics graphics) { // In ryan we trust
        RenderSystem.disableDepthTest();
        graphics.bufferSource().endBatch();
        RenderSystem.enableDepthTest();
        this.lineConsumer = null;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        calculateWavelength();
        int i = (this.width - imageWidth) / 2;
        int j = (this.height - imageHeight) / 2;
        pGuiGraphics.blit(BACKGROUND, i, j, 0, 0, imageWidth, imageHeight);
        renderSine(pGuiGraphics);
//        frequency = 5;
        if (mute) {
            pGuiGraphics.blit(SWITCH_ON, width / 2 + 72, height / 2 - 20, 0, 0, 10, 24, 10, 24);

        } else {
            pGuiGraphics.blit(SWITCH_OFF, width / 2 + 72, height / 2 - 20, 0, 0, 10, 24, 10, 24);
        }
        if (swap) {
            pGuiGraphics.blit(SWITCH_ON, width / 2 + 58, height / 2 - 20, 0, 0, 10, 24, 10, 24);

        } else {
            pGuiGraphics.blit(SWITCH_OFF, width / 2 + 58, height / 2 - 20, 0, 0, 10, 24, 10, 24);
        }
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void tick() {
        super.tick();
        if (!mute && frequency > .72f) {
            float pitch = Mth.getFromRange(0,30,2,.72f,frequency);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SuperpositionSounds.SINE.get(),pitch));
        }
        ticks++;
    }

    @Override
    public void onClose() {
        super.onClose();
        updateBlock();
    }
    public void updateBlock() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("frequency",frequency);
        tag.putBoolean("swap",swap);
        Messages.sendToServer(new BlockEntityModificationC2SPacket(tag,pos));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
