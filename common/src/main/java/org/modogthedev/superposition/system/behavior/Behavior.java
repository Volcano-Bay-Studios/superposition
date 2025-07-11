package org.modogthedev.superposition.system.behavior;

import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;

public abstract class Behavior {
    private ResourceLocation selfReference;

    public Behavior(ResourceLocation selfReference) {
        this.selfReference = selfReference;
    }

    public ResourceLocation getSelfReference() {
        return selfReference;
    }

}
