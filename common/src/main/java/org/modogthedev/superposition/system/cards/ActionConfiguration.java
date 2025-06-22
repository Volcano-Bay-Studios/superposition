package org.modogthedev.superposition.system.cards;

import foundry.veil.api.client.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ActionConfiguration {
    private Component title = null;

    public ActionConfiguration(Component title) {
        this.title = title;
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
        int topBackground = new Color().setInt(60, 186, 94, 60).argb();
        guiGraphics.drawString(Minecraft.getInstance().font,title,x,y,topBackground);
    }
}
