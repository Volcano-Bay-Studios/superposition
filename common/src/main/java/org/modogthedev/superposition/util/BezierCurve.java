package org.modogthedev.superposition.util;

import org.joml.Vector3d;

// Provided from the mod "Clinker" which is licensed under the MIT License.
public class BezierCurve {
    final Vector3d[] points = new Vector3d[4];

    public BezierCurve(Vector3d... points) {
        System.arraycopy(points, 0, this.points, 0, this.points.length);
    }

    public Vector3d evaluate(double t) {
        Vector3d lerp01 = points[0].lerp(points[1], t, new Vector3d());
        Vector3d lerp12 = points[1].lerp(points[2], t, new Vector3d());
        Vector3d lerp23 = points[2].lerp(points[3], t, new Vector3d());

        Vector3d lerp0112 = lerp01.lerp(lerp12, t);
        Vector3d lerp1223 = lerp23.lerp(lerp12, 1 - t);

        return lerp0112.lerp(lerp1223, t);
    }
}