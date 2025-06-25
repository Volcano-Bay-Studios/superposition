package org.modogthedev.superposition.system.card.actions.configuration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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
        String name = relativeName(Direction.values()[ordinal]);
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawCenteredString(font, name, 86, 20, topBorder);
        if (mouse) {
            float width = font.width(name);
            if (mouseX > 86) {
                String name1 = relativeName(Direction.values()[step(1)]);
                String name2 = relativeName(Direction.values()[step(-1)]);
                guiGraphics.drawCenteredString(font, ">", 106, 20, topBorder);
                guiGraphics.drawCenteredString(font, name1, 126, 20, bottomBorder);
                guiGraphics.drawCenteredString(font, name2, 46, 20, bottomBorder);
            } else {
                String name1 = relativeName(Direction.values()[step(-1)]);
                String name2 = relativeName(Direction.values()[step(1)]);
                guiGraphics.drawCenteredString(font, "<", 66, 20, topBorder);
                guiGraphics.drawCenteredString(font, name1, 46, 20, bottomBorder);
                guiGraphics.drawCenteredString(font, name2, 126, 20, bottomBorder);
            }
        }
    }

    public Direction value() {
        return Direction.values()[ordinal];
    }

    public Direction relative(Direction direction) {
        Direction self = value();
        switch (self) {
            case SOUTH -> {
                return direction.getClockWise().getClockWise();
            }
            case WEST -> {
                return direction.getCounterClockWise();
            }
            case EAST -> {
                return direction.getClockWise();
            }
        }
        return direction;
    }

    @Override
    public void mouse(int button, double x, double y) {
        if (x > 86) {
            ordinal = step(1);
        } else {
            ordinal = step(-1);
        }
    }

    public String relativeName(Direction direction) {
        switch (direction) {
            case UP -> {
                return "Top";
            }
            case DOWN -> {
                return "Bottom";
            }
            case NORTH -> {
                return "Front";
            }
            case SOUTH -> {
                return "Back";
            }
            case WEST -> {
                return "Left";
            }
            case EAST -> {
                return "Right";
            }
        }
        return "the abyss";
    }

    public int step(int amount) {
        int newOrdinal = (ordinal + amount) % 6;
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
        ordinal = Mth.clamp(loadTag.getInt("ordinal"),0,5);
        return loadTag;
    }
}
