package org.modogthedev.superposition.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.core.SuperpositionTags;

public class LongRaycast {
    public static float getPenetration(Level level, Vector3d from, Vector3d to) {
        float penetration = 0f;

        float length = (float) from.distance(to);
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (float f = 0; f < length; f++) {
            float delta = f / length;
            Vec3 pos = SuperpositionMth.lerpVector3d(from, to, delta);

            blockPos.set(Math.floor(pos.x), Math.floor(pos.y), Math.floor(pos.z));
            if (level.getChunk(blockPos).getSection(blockPos.getY() / 16).hasOnlyAir()) {
                f += 16f;
                continue;
            }

            if (level.isLoaded(blockPos)) {
                BlockState blockState = level.getBlockState(blockPos);
                if (!blockState.is(Blocks.AIR)) {
                    penetration += 0.1f;
                    if (blockState.is(SuperpositionTags.HARD_PENETRATE)) {
                        penetration += 100;
                    } else if (blockState.is(SuperpositionTags.MEDIUM_PENETRATE)) {
                        penetration += 10;
                    }
                }
            } else {
                f += 16;
            }
        }

        return penetration;
    }

}
