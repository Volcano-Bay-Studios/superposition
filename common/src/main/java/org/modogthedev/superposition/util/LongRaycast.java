package org.modogthedev.superposition.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.modogthedev.superposition.core.SuperpositionTags;

import java.util.ArrayList;
import java.util.List;

public class LongRaycast {
    public static float getPenetration(Level level, Vector3d from, Vector3d to) {
        float penetration = 0f;


        float length = (float) from.distance(to);
        List<BlockPos> posList = bresenham3D(from,to);
        for (int i = 0; i < posList.size();) {
            BlockPos blockPos = posList.get(i);
            if (level.getChunk(blockPos).getSection((blockPos.getY()-Math.min(0,level.getMinBuildHeight()))/ 16).hasOnlyAir()) {
                i += 16;
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
                 i += 16;
                 continue;
            }
            i++;
        }
        return penetration;
    }

    public static List<BlockPos> bresenham3D(Vector3d pos1, Vector3d pos2) {
        int x1 = (int) pos1.x;
        int y1 = (int) pos1.y;
        int z1 = (int) pos1.z;
        int x2 = (int) pos2.x;
        int y2 = (int) pos2.y;
        int z2 = (int) pos2.z;
        List<BlockPos> ListOfPoints = new ArrayList<>();
        ListOfPoints.add(new BlockPos(x1, y1, z1));
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);
        int xs;
        int ys;
        int zs;
        if (x2 > x1) {
            xs = 1;
        } else {
            xs = -1;
        }
        if (y2 > y1) {
            ys = 1;
        } else {
            ys = -1;
        }
        if (z2 > z1) {
            zs = 1;
        } else {
            zs = -1;
        }

        // Driving axis is X-axis"
        if (dx >= dy && dx >= dz) {
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;
            while (x1 != x2) {
                x1 += xs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;
                ListOfPoints.add(new BlockPos(x1, y1, z1));
            }

            // Driving axis is Y-axis"
        } else if (dy >= dx && dy >= dz) {
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;
            while (y1 != y2) {
                y1 += ys;
                if (p1 >= 0) {
                    x1 += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;
                ListOfPoints.add(new BlockPos(x1, y1, z1));
            }

            // Driving axis is Z-axis"
        } else {
            int p1 = 2 * dy - dz;
            int p2 = 2 * dx - dz;
            while (z1 != z2) {
                z1 += zs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x1 += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
                ListOfPoints.add(new BlockPos(x1, y1, z1));
            }
        }
        return ListOfPoints;
    }

}
