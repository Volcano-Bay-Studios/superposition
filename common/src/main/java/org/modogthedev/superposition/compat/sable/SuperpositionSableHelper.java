package org.modogthedev.superposition.compat.sable;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SuperpositionSableHelper {
    public static Vec3 transformPosition(Level level, Vec3 position) {
        return SableCompanion.INSTANCE.projectOutOfSubLevel(level, position);
    }

    public static Vec3 transformNormal(Level level, Vec3 pos, Vec3 normal) {
        SubLevel sublevel = Sable.HELPER.getContaining(level, pos);
        Pose3dc pose = null;
        if (sublevel != null) {
            if (sublevel instanceof ClientSubLevel clientSubLevel) {
                pose = clientSubLevel.renderPose();
            } else {
                pose = sublevel.logicalPose();
            }
        }
        if (pose != null) {
            return pose.transformNormal(normal);
        }
        return normal;
    }

    public static Quaternionf getRotation(Level level, Vec3 pos) {
        SubLevel sublevel = Sable.HELPER.getContaining(level, pos);
        Pose3dc pose = null;
        if (sublevel != null) {
            if (sublevel instanceof ClientSubLevel clientSubLevel) {
                pose = clientSubLevel.renderPose();
            } else {
                pose = sublevel.logicalPose();
            }
        }
        if (pose != null) {
            return new Quaternionf(pose.orientation());
        }
        return null;
    }

    public static Vec3 collide(Level level, Vec3 vel, Vec3 pos) {
        BoundingBox3d boundingBox = new BoundingBox3d(pos.subtract(1, 1, 1), pos.add(1, 1, 1));

        for (SubLevel sublevel : Sable.HELPER.getAllIntersecting(level, boundingBox)) {
            Pose3d pose = sublevel.logicalPose();
            vel = pose.transformNormalInverse(vel);
            Vec3 posTransformed = pose.transformPositionInverse(pos);


            Vec3 minBox = posTransformed.subtract(2 / 16f, 2 / 16f, 2 / 16f);
            Vec3 maxBox = posTransformed.add(2 / 16f, 2 / 16f, 2 / 16f);

            AABB collisionBox = new AABB(minBox, maxBox);

            List<AABB> collisions = new ArrayList<>();
            for (VoxelShape shape : level.getBlockCollisions(null, collisionBox.expandTowards(vel))) {
                collisions.addAll(shape.toAabbs());
            }
            if (!collisions.isEmpty()) {
                Vec3 transformCollide = pose.transformPosition(resolveCollisions(collisionBox, collisions));
                pos = transformCollide;
            }
        }
        return pos;
    }


    public static Vec3 resolveCollisions(AABB target, List<AABB> colliders) {
        Vector3f currentCenter = target.getCenter().toVector3f();

        for (AABB other : colliders) {
            if (target.intersects(other)) {
                float overlapX = (float) (Math.min(target.maxX, other.maxX) - Math.max(target.minX, other.minX));
                float overlapY = (float) (Math.min(target.maxY, other.maxY) - Math.max(target.minY, other.minY));
                float overlapZ = (float) (Math.min(target.maxZ, other.maxZ) - Math.max(target.minZ, other.minZ));
                if (overlapX < overlapY && overlapX < overlapZ) {
                    float dir = (target.getCenter().x < other.getCenter().x) ? -overlapX : overlapX;
                    target = target.move(dir, 0, 0);
                } else if (overlapY < overlapZ) {
                    float dir = (target.getCenter().y < other.getCenter().y) ? -overlapY : overlapY;
                    target = target.move(0, dir, 0);
                } else {
                    float dir = (target.getCenter().z < other.getCenter().z) ? -overlapZ : overlapZ;
                    target = target.move(0, 0, dir);
                }
            }
        }
        return target.getCenter();
    }
}
