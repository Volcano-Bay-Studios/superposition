package org.modogthedev.superposition.system.behavior;

import net.minecraft.resources.ResourceLocation;

public abstract class Behavior {
    private ResourceLocation selfReference;

    public Behavior(ResourceLocation selfReference) {
        this.selfReference = selfReference;
    }

    public ResourceLocation getSelfReference() {
        return selfReference;
    }

    public String getPath() {
        return getSelfReference().getPath();
    }
}
