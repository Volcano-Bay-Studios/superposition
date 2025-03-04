package org.modogthedev.superposition.system.cable.rope_system;

import net.minecraft.world.phys.Vec3;

public interface BendConstraint {

    static Vec3 resolve(Vec3 from, Vec3 middle, Vec3 to, float width) {
        return resolve(from, middle, to, width, 1.5f);
    }

//    static Vec3 resolve(Vec3 from, Vec3 middle, Vec3 to, float width, float widthFactor) {
//        //Push 'to' away from 'from';
//        double dist = from.distanceTo(to);
//
//        if (dist == 0) return to;
//
//        double pushFactor = (width * widthFactor) - dist;
//        pushFactor = Math.max(pushFactor, 0);
//        Vec3 midpoint = from.lerp(to, 0.5);
//        Vec3 newTo = to.add(to.subtract(midpoint).normalize().scale(pushFactor));
//
//        //Apply width
//        newTo = middle.add(newTo.subtract(middle).normalize().scale(width));
//
//        return newTo;
//    }

    static Vec3 resolve(Vec3 from, Vec3 middle, Vec3 to, float width, float widthFactor) {
        Vec3 toDirection = to.subtract(middle);
        Vec3 targetDirection = from.subtract(middle).scale(-1).normalize();

        double currentLength = middle.distanceToSqr(to);

        Vec3 targetTo = middle.add(targetDirection.scale(currentLength));

        double distance = toDirection.distanceToSqr(targetDirection);

        Vec3 resultingPosition = to.lerp(targetTo, Math.clamp(Math.max(distance, 0.75) - 0.75, 0, 1));

        return resultingPosition.subtract(middle).normalize().scale(width).add(middle);
    }

    static Vec3 resolveAnchorBend(Vec3 from, Vec3 middle, Vec3 to, float width) {
        Vec3 targetDirection = from.subtract(middle).scale(-1).normalize();

        double currentLength = middle.distanceToSqr(to);
        Vec3 resultingPosition = to.lerp(middle.add(targetDirection.scale(currentLength)), 0.9f);

        return resultingPosition.subtract(middle).normalize().scale(width).add(middle);
    }

    static Vec3 resolveSinglePair(Vec3 middle, Vec3 to, float width) {
        Vec3 newTo = to;

        //Apply width
        newTo = middle.add(newTo.subtract(middle).normalize().scale(width));

        return newTo;
    }

}
