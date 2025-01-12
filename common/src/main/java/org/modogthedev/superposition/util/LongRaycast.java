package org.modogthedev.superposition.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class LongRaycast {
    public static float getPenetration(Level level, Vector3d from, Vector3d to) {
        float penetration = 0f;

        float length = (float) from.distance(to);
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (float f = 0; f < length; f++) {
            float delta = f/length;
            Vec3 pos = SuperpositionMth.lerpVector3d(from,to,delta);

            blockPos.set(pos.x,pos.y,pos.z);
            if (level.getChunk(blockPos).getSection(blockPos.getY()/16).hasOnlyAir()) {
                f += 16f;
                continue;
            }
            if (level.isLoaded(blockPos)) {
                if (!level.getBlockState(blockPos).is(Blocks.AIR)) {
                    penetration += 0.1f;
                }
            } else {
                f += 16;
            }
        }

        return penetration;
    }
}
