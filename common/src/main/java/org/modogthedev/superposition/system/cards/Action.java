package org.modogthedev.superposition.system.cards;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class Action {
    private final ResourceLocation selfReference;

    public Action(ResourceLocation action) {
        this.selfReference = action;
    }

    public ResourceLocation getSelfReference() {
        return this.selfReference;
    }

    public ItemStack getThumbnailItem() {
        return null;
    }
}
