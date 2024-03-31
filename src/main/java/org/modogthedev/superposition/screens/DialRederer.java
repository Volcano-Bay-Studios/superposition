package org.modogthedev.superposition.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.Superposition;

public class DialRederer {
    public static ResourceLocation dial = new ResourceLocation(Superposition.MODID,"textures/screen/dial.png");
    public static void renderDail(GuiGraphics guiGraphics, int x,int y, int index) {
        if (index > 23) {

            index = (int) (index - (Math.floor(index/23)*23));
        } else if (index < 0) {
            index = (int) (index - (Math.floor(index/23)*23))+23;
        }
        guiGraphics.blit(dial,x-10,y-10,DialUV.values()[index].UVX,DialUV.values()[index].UVY,20,21,60,168);
    }
    public enum DialUV {
        ZERO(0,0),
        ONE(20,0),
        TWO(0,21),
        THREE(20,21),
        FOUR(0,42),
        FIVE(20,42),
        SIX(0,63),
        SEVEN(20,63),
        EIGHT(0,84),
        NINE(20,84),
        TEN(0,105),
        ELEVEN(20,105),
        TWELVE(0,126),
        THIRTEEN(20,126),
        FOURTEEN(0,147),
        FIFTEEN(20,147),
        SIXTEEN(40,0),
        SEVENTEEN(40,21),
        EIGHTEEN(40,42),
        NINETEEN(40,63),
        TWENTY(40,84),
        TWENTYONE(40,105),
        TWENTYTWO(40,126),
        TWENTYTHREE(40,147);
        DialUV(int uvX, int uvY) {
            this.UVX = uvX;
            this.UVY = uvY;
        }
        public int UVX;
        public int UVY;
    }
}
