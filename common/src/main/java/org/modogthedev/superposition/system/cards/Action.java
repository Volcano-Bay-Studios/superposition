package org.modogthedev.superposition.system.cards;

import net.minecraft.resources.ResourceLocation;

public class Action {
    private final ResourceLocation selfReference;

    public Action(ResourceLocation action) {
        this.selfReference = action;
    }

    public ResourceLocation getSelfReference() {
        return this.selfReference;
    }
}
