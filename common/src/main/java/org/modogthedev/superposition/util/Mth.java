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
    public static float resonanceAlgorithm(int n1, int n2) {
        if (n1 > n2) { // Denominator will always be n2
            int temp = n2;
            n2 = n1;
            n1 = temp;
        }
        if (n1 == n2)
            return 1;
        if (n2 % n1 != 0) {
            return 0;
        }
        return (float) n1 / n2;
    }
    public static int gcdByEuclidsAlgorithm(int n1, int n2) {
        if (n2 == 0) {
            return n1;
        }
        return gcdByEuclidsAlgorithm(n2, n1 % n2);
    }
    public static int gcdBySteinsAlgorithm(int n1, int n2) {
        if (n1 == 0) {
            return n2;
        }

        if (n2 == 0) {
            return n1;
        }

        int n;
        for (n = 0; ((n1 | n2) & 1) == 0; n++) {
            n1 >>= 1;
            n2 >>= 1;
        }

        while ((n1 & 1) == 0) {
            n1 >>= 1;
        }

        do {
            while ((n2 & 1) == 0) {
                n2 >>= 1;
            }

            if (n1 > n2) {
                int temp = n1;
                n1 = n2;
                n2 = temp;
            }
            n2 = (n2 - n1);
        } while (n2 != 0);
        return n1 << n;
    }
}
