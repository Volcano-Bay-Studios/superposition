package org.modogthedev.superposition.system.cards.actions.configuration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.modogthedev.superposition.Superposition;

public class DirectionConfiguration extends ActionConfiguration {
    private int ordinal;

    public DirectionConfiguration(Component title) {
        super(title);
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.render(guiGraphics, mouseX, mouseY);

        int topBorder = Superposition.SUPERPOSITION_THEME.get("topBorder");
        int bottomBorder = Superposition.SUPERPOSITION_THEME.get("bottomBorder");
        boolean mouse = mouseX > 0 && mouseY > 0 && mouseY < getHeight();
        String name = Direction.values()[ordinal].getName();
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawCenteredString(font, name, 86, 20, topBorder);
        if (mouse) {
            float width = font.width(name);
            if (mouseX > 86) {
                String name1 = Direction.values()[step(1)].getName();
                String name2 = Direction.values()[step(-1)].getName();
                guiGraphics.drawCenteredString(font, ">", (int) (106), 20, topBorder);
                guiGraphics.drawCenteredString(font, name1, (int) (126), 20, bottomBorder);
                guiGraphics.drawCenteredString(font, name2, (int) (46), 20, bottomBorder);
            } else {
                String name1 = Direction.values()[step(-1)].getName();
                String name2 = Direction.values()[step(1)].getName();
                guiGraphics.drawCenteredString(font, "<", (int) (66), 20, topBorder);
                guiGraphics.drawCenteredString(font, name1, (int) (46), 20, bottomBorder);
                guiGraphics.drawCenteredString(font, name2, (int) (126), 20, bottomBorder);
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
        int newOrdinal = (ordinal + amount) % 6;
        if (newOrdinal < 0) {
            newOrdinal = 5;
        }
        return newOrdinal;
    }
}
