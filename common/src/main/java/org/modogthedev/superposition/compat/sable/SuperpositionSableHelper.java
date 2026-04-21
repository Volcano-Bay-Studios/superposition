package org.modogthedev.superposition.compat.sable;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

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

}
