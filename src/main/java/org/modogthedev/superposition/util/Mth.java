package org.modogthedev.superposition.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Mth {
    public static float getFromRange(float OldMax, float OldMin, float NewMax, float NewMin, float OldValue) {
        float OldRange = (OldMax - OldMin);
        float NewRange = (NewMax - NewMin);
        return  (((OldValue - OldMin) * NewRange) / OldRange) + NewMin;
    }
    public static BlockPos blockPosFromVec3(Vec3 vec3) {
        return new BlockPos((int) vec3.x, (int) vec3.y, (int) vec3.z);
    }

    /**
     * Returns the hertz value of a given frequency in a string that is readable
     * @param frequency
     * @return String in hertz
     */
    public static String frequencyToHzReadable(float frequency) {
        if (frequency >=1000000000) {
            return Math.round(frequency/100000000)+"GHz";
        } else if (frequency >=1000000) {
            return Math.round(frequency/100000)+"MHz";
        } else if (frequency >=1000) {
            return Math.round(frequency/100)+"kHz";
        }
        return frequency+"Hz";
    }

    /**
     * Returns the frequency of an antenna
     * See <a href="https://www.ahsystems.com/EMC-formulas-equations/frequency-wavelength-calculator.php">...</a>
     * @param size how many blocks the antenna is
     * @return The antenna frequency in Hz
     */
    public static int antennaSizeToHz(int size) {
        return (int) ((14989622)/(size/2f));
    }
    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};

        int times = (to.ordinal() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }
}
