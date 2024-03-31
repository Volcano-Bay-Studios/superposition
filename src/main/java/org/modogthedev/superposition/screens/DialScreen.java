package org.modogthedev.superposition.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class DialScreen extends Screen {
    public List<Dial> dials = new ArrayList<>();
    public boolean mouseDown;
    protected DialScreen(Component pTitle) {
        super(pTitle);
        setPositions();
    }
    public void addDial(int x, int y) {
        dials.add(new Dial(x, y));
    }
    public void setPositions() {
        for (Dial dial: dials) {
            dial.x = (this.width/2) + dial.targetx;
            dial.y = (this.height/2) + dial.targety;
        }
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        setPositions();
        for (Dial dial: dials) {
            DialRederer.renderDail(pGuiGraphics,dial.x,dial.y, (int) dial.scrolledAmount);
        }
        if (mouseDown) {
            for (Dial dial: dials) {
                if (dial.mouseOver) {
                    float angle = (float) Math.toDegrees(Math.atan2(pMouseY-dial.y,pMouseX-dial.x));
                    if (angle < 0) {
                        angle = Mth.getFromRange(0,-180,180,0,angle)+180;
                    }
                    if (dial.lastAngle == 0 || Math.abs(dial.lastAngle-angle) > 300)
                        dial.lastAngle = angle;
                    else {
                        if (Math.abs(dial.lastAngle-angle) > 1)
                            playScrollSound(Minecraft.getInstance().getSoundManager());
                        dial.scrolledAmount += (dial.lastAngle-angle)/10;
                        dial.lastAngle = angle;
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        mouseDown = true;
        getTouching((int) pMouseX, (int) pMouseY);
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        mouseDown = false;
        for (Dial dial: dials) {
            dial.mouseOver = false;
            dial.lastAngle = 0;
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        getTouching((int) pMouseX, (int) pMouseY);
        for (Dial dial: dials) {
            if (dial.mouseOver) {
                dial.scrolledAmount += pDelta;
                playScrollSound(Minecraft.getInstance().getSoundManager());
            }
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }
    public void playScrollSound(SoundManager pHandler) {
        pHandler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PUT, 1.0F));
    }
    public void getTouching(int x, int y) {
        for (Dial dial : dials) {
            dial.mouseOver = (dial.x > x - dial.size / 2 && dial.x < x + dial.size / 2 && dial.y > y - dial.size / 2 && dial.y < y + dial.size / 2);
        }
    }

    public static class Dial {
        public Dial(int x, int y) {
            this.targetx = x;
            this.targety = y;
        }
        int targetx;
        int targety;
        int x;
        int y;
        int size = 21;
        float lastAngle;
        boolean mouseOver;
        float scrolledAmount;
    }
}
