package org.modogthedev.superposition.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class Mth {
    public static float getFromRange(float OldMax, float OldMin, float NewMax, float NewMin, float OldValue) {
        float OldRange = (OldMax - OldMin);
        float NewRange = (NewMax - NewMin);
        return  (((OldValue - OldMin) * NewRange) / OldRange) + NewMin;
    }
    public static BlockPos blockPosFromVec3(Vec3 vec3) {
        return new BlockPos((int) vec3.x, (int) vec3.y, (int) vec3.z);
    }
}
