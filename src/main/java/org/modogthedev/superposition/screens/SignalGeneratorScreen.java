package org.modogthedev.superposition.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.event.ClientEvents;

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
    public boolean mute = true;
    public boolean swap = false;

    public SignalGeneratorScreen(Component pTitle, BlockPos pos) {
        super(pTitle);
        SignalGeneratorScreen.pos = pos;
        ticks = 0;
        addDial(-25,0);
        addDial(25,0);
        swap = Minecraft.getInstance().level.getBlockState(pos).getValue(SignalGeneratorBlock.SWAP_SIDES);
    }
    public void playDownSound(SoundManager pHandler) {
        pHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void drawPixel(GuiGraphics pGuiGraphics, int x, int y) {
        pGuiGraphics.blit(PIXEL, x, y, 0, 0, 1, 1);
    }

    public void renderSine(GuiGraphics pGuiGraphics) {
        int startPos = (this.width - 158) / 2;
        int j = (this.height - imageHeight) / 2;
        int width = this.width;
        for (float i = 0; i < 158; i += .05f) {
            int calculatedPosition = (int) (Math.sin((double) (i + ticks) / frequency) * 25);
            drawPixel(pGuiGraphics, (int) (i + (startPos)), (j + 45 + calculatedPosition));
        }
        if (frequency < 1) {
            pGuiGraphics.blit(WARN_ON,width/2-81,height/2-20,0,0,14,14,14,14);
        } else {
            pGuiGraphics.blit(WARN_OFF,width/2-81,height/2-20,0,0,14,14,14,14);
        }
    }
    public void calculateWavelength() {
        frequency = Math.abs(1 + (dials.get(0).scrolledAmount/10) + (dials.get(1).scrolledAmount));
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if ((double) width /2+72 > pMouseX - 10 && (double) width /2+72 < pMouseX && (double) height /2-20 > pMouseY - 24 && (double) height /2-20 < pMouseY) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            mute = !mute;
        }
        if ((double) width /2+58 > pMouseX - 10 && (double) width /2+60 < pMouseX && (double) height /2-20 > pMouseY - 24 && (double) height /2-20 < pMouseY) {
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

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        calculateWavelength();
        int i = (this.width - imageWidth) / 2;
        int j = (this.height - imageHeight) / 2;
        pGuiGraphics.blit(BACKGROUND, i, j, 0, 0, imageWidth, imageHeight);
        renderSine(pGuiGraphics);
        frequency = 5;
        if (mute) {
            pGuiGraphics.blit(SWITCH_ON,width/2+72,height/2-20,0,0,10,24,10,24);

        } else {
            pGuiGraphics.blit(SWITCH_OFF,width/2+72,height/2-20,0,0,10,24,10,24);
        }
        if (swap) {
            pGuiGraphics.blit(SWITCH_ON,width/2+58,height/2-20,0,0,10,24,10,24);

        } else {
            pGuiGraphics.blit(SWITCH_OFF,width/2+58,height/2-20,0,0,10,24,10,24);
        }

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
}
