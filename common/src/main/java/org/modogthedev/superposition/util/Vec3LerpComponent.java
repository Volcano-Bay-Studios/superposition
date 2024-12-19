package org.modogthedev.superposition.util;

import net.minecraft.world.phys.Vec3;

public class Vec3LerpComponent {
    private int steps;
    private int step;
    private Vec3 to;
    private Vec3 from;

    public Vec3LerpComponent(Vec3 to, Vec3 from, int steps) {
        this.to = to;
        this.from = from;
        this.steps = steps;
    }

    public void step() {
        this.step += 1;
    }

    public boolean isComplete() {
        return step >= steps;
    }

    public Vec3 getLerpedPos() {
        return SuperpositionMth.lerpVec3(from, to, (float) step / steps);
    }

    public Vec3 stepAndGather() {
        step();
        return getLerpedPos();
    }
}
