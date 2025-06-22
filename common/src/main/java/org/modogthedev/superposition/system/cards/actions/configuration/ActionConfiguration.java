package org.modogthedev.superposition.system.cards.actions.configuration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionActions;

public class ActionConfiguration {
    private Component title = null;

    public ActionConfiguration(Component title) {
        this.title = title;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int topBorder = Superposition.SUPERPOSITION_THEME.get("topBorder");
        guiGraphics.drawString(Minecraft.getInstance().font, title, 0, 0, topBorder);
    }

    public void mouse(int button, double x, double y) {

    }

    public int getHeight() {
        return 50;
    }

    public CompoundTag save(CompoundTag tag) {
        SuperpositionActions.ACTION_CONFIGURATIONS.asVanillaRegistry().getKey(this);
        return tag;
    }
    public CompoundTag load(CompoundTag tag) {
        return tag;
    }
}
