package org.modogthedev.superposition.system.card.actions.configuration.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.card.actions.configuration.ClientConfigContext;
import org.modogthedev.superposition.system.card.actions.configuration.StringConfiguration;

public class ClientStringConfigurationContext extends ClientConfigContext {
    private EditBox box;

    public ClientStringConfigurationContext(StringConfiguration configuration) {
        int topBorder = Superposition.SUPERPOSITION_THEME.get("topBorder");
        box = new EditBox(Minecraft.getInstance().font,100,16, Component.literal("Config Field"));
        box.setValue(configuration.getString());
        box.setPosition(16,16);
        box.setBordered(false);
        box.setMaxLength(configuration.maxLength());
        box.setTextColor(topBorder);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        box.render(guiGraphics,mouseX,mouseY,0);
    }

    @Override
    public boolean mouse(int button, double x, double y) {
        boolean consume = box.mouseClicked(x, y, button);
        if (consume) {
            box.setFocused(true);
        }
        return consume;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return box.keyPressed(keyCode,scanCode,modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return box.charTyped(codePoint,modifiers);
    }

    @Override
    public void looseFocus() {
        box.setFocused(false);
    }

    public String getText() {
        return box.getValue();
    }

    public void setText(String text) {
        box.setValue(text);
    }
}
