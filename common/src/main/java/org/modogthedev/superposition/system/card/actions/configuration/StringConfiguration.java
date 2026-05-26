package org.modogthedev.superposition.system.card.actions.configuration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.card.actions.configuration.client.ClientStringConfigurationContext;

public class StringConfiguration extends ActionConfiguration {
    private ClientStringConfigurationContext context = null;
    private String text = "field";
    public StringConfiguration(Component title) {
        super(title);
    }

    public ClientStringConfigurationContext getContext() {
        if (context == null) {
            context = new ClientStringConfigurationContext(this);
        }
        return context;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.render(guiGraphics, mouseX, mouseY);
        getContext().render(guiGraphics,mouseX,mouseY);
    }

    public String getString() {
        return text;
    }

    public int maxLength() {
        return 40;
    }

    @Override
    public boolean mouse(int button, double x, double y) {
        return getContext().mouse(button,x,y);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return getContext().charTyped(codePoint,modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return getContext().keyPressed(keyCode,scanCode,modifiers);
    }

    @Override
    public void looseFocus() {
       getContext().looseFocus();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag saveTag = super.save(tag);
        if (context != null) {
            text = context.getText();
        }
        saveTag.putString("port", text);

        return saveTag;
    }

    @Override
    public CompoundTag load(CompoundTag tag) {
        CompoundTag loadTag = super.load(tag);
        text = loadTag.getString("port");
        if (context != null) {
            context.setText(text);
        }
        return loadTag;
    }
}