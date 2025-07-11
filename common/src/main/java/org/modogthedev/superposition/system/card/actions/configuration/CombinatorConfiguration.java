package org.modogthedev.superposition.system.card.actions.configuration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.modogthedev.superposition.Superposition;

public class CombinatorConfiguration extends ActionConfiguration {
    private int ordinal;
    private final Enum<?>[] enumConfig;

    public CombinatorConfiguration(Component title, Enum<?>[] enumConfig) {
        super(title);
        this.enumConfig = enumConfig;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.render(guiGraphics, mouseX, mouseY);

        int topBorder = Superposition.SUPERPOSITION_THEME.get("topBorder");
        int bottomBorder = Superposition.SUPERPOSITION_THEME.get("bottomBorder");
        boolean mouse = mouseX > 0 && mouseY > 0 && mouseY < getHeight();
        String name = enumConfig[ordinal].name();
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawCenteredString(font, name, 86, 13, topBorder);
        if (mouse) {
            float width = font.width(name);
            if (mouseX > 86) {
                String name1 = enumConfig[step(1)].name();
                String name2 = enumConfig[step(-1)].name();
                guiGraphics.drawCenteredString(font, ">", 106, 13, topBorder);
                guiGraphics.drawCenteredString(font, name1, 126, 13, bottomBorder);
                guiGraphics.drawCenteredString(font, name2, 46, 13, bottomBorder);
            } else {
                String name1 = enumConfig[step(-1)].name();
                String name2 = enumConfig[step(1)].name();
                guiGraphics.drawCenteredString(font, "<", 66, 13, topBorder);
                guiGraphics.drawCenteredString(font, name1, 46, 13, bottomBorder);
                guiGraphics.drawCenteredString(font, name2, 126, 13, bottomBorder);
            }
        }
    }

    public Direction value() {
        return Direction.values()[ordinal];
    }


    @Override
    public void mouse(int button, double x, double y) {
        if (x > 86) {
            ordinal = step(1);
        } else {
            ordinal = step(-1);
        }
    }

    public int step(int amount) {
        int newOrdinal = (ordinal + amount) % enumConfig.length;
        if (newOrdinal < 0) {
            newOrdinal = 5;
        }
        return newOrdinal;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag saveTag = super.save(tag);
        saveTag.putInt("ordinal",ordinal);
        return saveTag;
    }

    @Override
    public CompoundTag load(CompoundTag tag) {
        CompoundTag loadTag = super.load(tag);
        ordinal = Mth.clamp(loadTag.getInt("ordinal"),0,enumConfig.length-1);
        return loadTag;
    }
}
