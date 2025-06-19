package org.modogthedev.superposition.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface SPTooltipable {
    List<Component> getTooltip();

    boolean isTooltipEnabled();

    boolean isSuperpositionTooltipEnabled();


    void setTooltip(List<Component> var1);

    void addTooltip(Component var1);

    void addTooltip(List<Component> var1);

    void addTooltip(String var1);


    void setBackgroundColor(int var1);

    void setTopBorderColor(int var1);

    void setBottomBorderColor(int var1);

    void drawExtra();

    boolean getWorldspace();

    ItemStack getStack();

    int getTooltipWidth();

    int getTooltipHeight();

    int getTooltipXOffset();

    int getTooltipYOffset();

}
