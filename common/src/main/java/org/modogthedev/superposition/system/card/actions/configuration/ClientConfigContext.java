package org.modogthedev.superposition.system.card.actions.configuration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.modogthedev.superposition.Superposition;

public abstract class ClientConfigContext {
    public ClientConfigContext() {

    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    public boolean mouse(int button, double x, double y) {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    public void looseFocus() {}

    public void tick(int animation) {
    }

    public CompoundTag save(CompoundTag tag) {
        return tag;
    }

    public CompoundTag load(CompoundTag tag) {
        return tag;

    }
}
