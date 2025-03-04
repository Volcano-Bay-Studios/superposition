package org.modogthedev.superposition.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.modogthedev.superposition.system.signal.Signal;

public class SuperpositionMth {
    public static float getFromRange(float OldMax, float OldMin, float NewMax, float NewMin, float OldValue) {
        float OldRange = (OldMax - OldMin);
        float NewRange = (NewMax - NewMin);
        return (((OldValue - OldMin) * NewRange) / OldRange) + NewMin;
    }

    public static BlockPos blockPosFromVec3(Vec3 vec3) {
        return new BlockPos((int) vec3.x, (int) vec3.y, (int) vec3.z);
    }

    public static BlockPos blockPosFromVec3(Vector3dc vec3) {
        return new BlockPos((int) vec3.x(), (int) vec3.y(), (int) vec3.z());
    }

    public static Vec3 lerpVec3(Vec3 start, Vec3 end, float delta) {
        return new Vec3(Mth.lerp(delta, start.x, end.x), Mth.lerp(delta, start.y, end.y), Mth.lerp(delta, start.z, end.z));
    }

    public static Vec3 lerpVector3d(Vector3d start, Vector3d end, float delta) {
        return new Vec3(Mth.lerp(delta, start.x, end.x), Mth.lerp(delta, start.y, end.y), Mth.lerp(delta, start.z, end.z));
    }

    public static Vector3dc convertVec(BlockPos pos) {
        return new Vector3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
    }

    /**
     * Returns the hertz value of a given frequency in a string that is readable
     *
     * @param frequency
     * @return String in hertz
     */
    public static String frequencyToHzReadable(float frequency) {
        if (frequency >= 1000000000) {
            return Math.round(frequency / 100000000) + "GHz";
        } else if (frequency >= 10000) {
            return String.format("%.3f", (frequency / 100000f)) + "MHz";
        } else if (frequency >= 1000) {
            return Math.round(frequency / 100) + "kHz";
        }
        return (Math.round(frequency * 1000)) / 1000f + "Hz";
    }

    /**
     * Returns the frequency of an antenna
     * See <a href="https://www.ahsystems.com/EMC-formulas-equations/frequency-wavelength-calculator.php">...</a>
     *
     * @param size how many blocks the antenna is
     * @return The antenna frequency in Hz
     */
    public static int antennaSizeToHz(int size) {
        return (int) ((14989622) / (size / 2f));
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

    private static boolean[] findIndexes(int n, int r) {
        boolean[] arrayWithObjects = new boolean[n];
        if (r < 2) {
            arrayWithObjects[7] = true;
            return arrayWithObjects;
        }

        int quotient = (n - 1) / (r - 1);
        int remainder = (n - 1) % (r - 1);

        int index = 0;
        do {
            arrayWithObjects[index] = true;
        } while ((index += quotient + (remainder-- > 0 ? 1 : 0)) < n);

        return arrayWithObjects;
    }

    public static Signal[] spaceArray(Signal[] signals, int size) {
        Signal[] signals1 = new Signal[size];
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (signals[(int) SuperpositionMth.getFromRange(size, 0, 12, 0, i)] != null)
                count++;
        }
        boolean[] booleans = findIndexes(size, count);
        int i = 0;
        for (int x = 0; x < size; x++) {
            if (booleans[x]) {
                signals1[x] = signals[i];
                i++;
            }
        }
        return signals1;
    }
}
