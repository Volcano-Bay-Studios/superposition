package org.modogthedev.superposition.util;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class CatmulRomSpline {
    public static List<Vec3> generateSpline(List<Vec3> controlPoints, int numSegments) {
        List<Vec3> splinePoints = new ArrayList<>();

        for (int i = 1; i < controlPoints.size() - 2; i++) {
            for (int j = 0; j < numSegments; j++) {
                double t = j / (double) numSegments;
                Vec3 point = interpolate(controlPoints.get(i - 1), controlPoints.get(i),
                        controlPoints.get(i + 1), controlPoints.get(i + 2), t);
                splinePoints.add(point);
            }
        }

        return splinePoints;
    }

    private static Vec3 interpolate(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, double t) {
        double t2 = t * t;
        double t3 = t2 * t;

        double a = -0.5 * t3 + t2 - 0.5 * t;
        double b = 1.5 * t3 - 2.5 * t2 + 1;
        double c = -1.5 * t3 + 2 * t2 + 0.5 * t;
        double d = 0.5 * t3 - 0.5 * t2;

        return p0.scale(a).add(p1.scale(b)).add(p2.scale(c)).add(p3.scale(d));
    }
}
