package org.modogthedev.superposition.system.widget;

import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3d;

/**
 * The area of a host that widgets are placed on.
 *
 * @param transform Transforms from the surface to the block. On the surface x and z are across it in blocks, and y is the distance from it.
 * @param minX      The bounds widgets must be within, in pixels
 */
public record WidgetSurface(Matrix4f transform, int minX, int maxX, int minY, int maxY) {

    /**
     * @param pos A position relative to the block
     * @return The position on the surface
     */
    public Vector3d toSurface(Vector3d pos) {
        return new Vector3d(pos).mulPosition(new Matrix4f(transform).invert());
    }

    /**
     * @return The closest position to the given position that keeps the widget on the surface
     */
    public Vector2i clamp(Vector2i pos, Widget widget) {
        return new Vector2i((int) Mth.clamp(pos.x, minX, maxX - widget.getBounds().x * 16), (int) Mth.clamp(pos.y, minY, maxY - widget.getBounds().z * 16));
    }

    public boolean contains(Vector2i pos, Widget widget) {
        return clamp(pos, widget).equals(pos);
    }
}
