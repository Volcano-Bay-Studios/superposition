package org.modogthedev.superposition.system.cable.rope_system;

import net.minecraft.world.phys.Vec3;

public interface BendConstraint {

    static Vec3 resolve(Vec3 from, Vec3 middle, Vec3 to, float width) {
        Vec3 targetDirection = from.subtract(middle).scale(-1).normalize();

        Vec3 targetTo = middle.add(targetDirection.scale(width));

        return to.lerp(targetTo, Math.clamp(to.distanceToSqr(targetTo), 0, 1) / 10f);
    }
    
    static Vec3 resolveAnchorBend(Vec3 from, Vec3 middle, Vec3 to, float width) {
        Vec3 targetDirection = from.subtract(middle).scale(-1).normalize();

        Vec3 resultingPosition = to.lerp(middle.add(targetDirection), 0.5f);
        
        return resultingPosition.subtract(middle).normalize().scale(width).add(middle);
    }
    
}
