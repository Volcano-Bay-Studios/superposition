package org.modogthedev.superposition.system.card.actions.configuration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.modogthedev.superposition.Superposition;

public class StringConfiguration extends ActionConfiguration {
    private String string = "text";
    private boolean focused = false;
    private int animation;

    public StringConfiguration(Component title) {
        super(title);
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.render(guiGraphics, mouseX, mouseY);

        int topBorder = Superposition.SUPERPOSITION_THEME.get("topBorder");
        int bottomBorder = Superposition.SUPERPOSITION_THEME.get("bottomBorder");
        boolean mouse = mouseX > 0 && mouseY > 0 && mouseY < getHeight();
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawCenteredString(font, string + (focused && animation > 20 ? "|" : ""), 86, 13, (mouse || focused) ? topBorder : bottomBorder);
    }

    public String getString() {
        string = string.substring(0, Mth.clamp(string.length(), 0,maxLength()));
        return string;
    }

    public int maxLength() {
        return 40;
    }

    @Override
    public boolean mouse(int button, double x, double y) {
        boolean mouse = x > 0 && y > 0 && y < getHeight();
        focused = mouse;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focused) {
            if (keyCode == 256) {
                focused = false;
                return true;
            }
            if (keyCode == 261 || keyCode == 259) {
                string = string.substring(0, Math.max(0, string.length() - 1));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (focused) {
            string += codePoint;
            string = string.substring(0, Mth.clamp(string.length(), 0,maxLength()));
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void tick(int animation) {
        this.animation = animation;
        string = string.substring(0, Mth.clamp(string.length(), 0,maxLength()));
        super.tick(animation);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag saveTag = super.save(tag);
        saveTag.putString("port", string);
        return saveTag;
    }

    @Override
    public CompoundTag load(CompoundTag tag) {
        CompoundTag loadTag = super.load(tag);
        string = loadTag.getString("port");
        return loadTag;
    }
}