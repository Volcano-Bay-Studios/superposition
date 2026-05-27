package org.modogthedev.superposition.compat.sable;

import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4d;
import org.joml.Quaternionf;
import org.modogthedev.superposition.compat.CompatabilityHandler;

public class SableCompat {
    public static Vec3 tryTransform(Level level, Vec3 pos) {
        if (CompatabilityHandler.Mod.SABLE.isLoaded) {
            return SuperpositionSableHelper.transformPosition(level,pos);
        }
        return pos;
    }

    public static Vec3 tryTransform(Level level, Vec3 sourcePos, Vec3 pos) {
        if (CompatabilityHandler.Mod.SABLE.isLoaded) {
            return SuperpositionSableHelper.transformPosition(level,sourcePos, pos);
        }
        return pos;
    }

    public static Vec3 tryTransformPositionInverse(Level level, Vec3 sourcePos, Vec3 pos) {
        if (CompatabilityHandler.Mod.SABLE.isLoaded) {
            return SuperpositionSableHelper.transformPositionInverse(level,sourcePos, pos);
        }
        return pos;
    }

    public static Vec3 transformNormal(Level level, Vec3 pos, Vec3 normal) {
        if (CompatabilityHandler.Mod.SABLE.isLoaded) {
            return SuperpositionSableHelper.transformNormal(level,pos,normal);
        }
        return normal;
    }

    public static boolean isSublevel(Level level, Vec3 pos) {
        return SableCompanion.INSTANCE.isInPlotGrid(level,pos);
    }

    public static @Nullable Quaternionf getRotation(Level level, Vec3 pos, float partialTick) {
        if (CompatabilityHandler.Mod.SABLE.isLoaded) {
            return SuperpositionSableHelper.getRotation(level,pos, partialTick);
        }
        return null;
    }


    public static @Nullable Matrix4d getTransformMatrix(Level level, BlockPos pos) {
        if (CompatabilityHandler.Mod.SABLE.isLoaded) {
            return SuperpositionSableHelper.getTransformMatrix(level,pos);
        }
        return null;
    }

    public static Vec3 tryCollide(Level level, Vec3 vel, Vec3 pos) {
        if (CompatabilityHandler.Mod.SABLE.isLoaded) {
            return SuperpositionSableHelper.collide(level,vel, pos);
        }
        return pos;
    }
}
