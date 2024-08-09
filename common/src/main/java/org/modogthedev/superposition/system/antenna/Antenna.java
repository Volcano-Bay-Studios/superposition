package org.modogthedev.superposition.system.antenna;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;

public class Antenna {
    public Level level;
    public List<BlockPos> antennaParts = new ArrayList<>();
    public List<Signal> signals = new ArrayList<>();
    public BlockPos antennaActor;
    public boolean reading;
    public Vec3 avg;
    public Vec3 size;
    public Vec3 lowSize;
    public Vec3 highSize;
    public Vec3 relativeCenter;

    public Antenna(List<BlockPos> antennaParts, BlockPos antennaActor, Level level) {
        this.antennaParts = antennaParts;
        this.antennaActor = antennaActor;
        this.level = level;
    }

    public boolean isPos(BlockPos pos) {
        return antennaActor.equals(pos);
    }

    public Vec3 getAvg() {
        Vec3 avg = Vec3.ZERO;
        for (BlockPos pos : antennaParts) {
           BlockPos relative = antennaActor.subtract(pos);
           BlockPos abs = new BlockPos(Math.abs(relative.getX()),Math.abs(relative.getY()),Math.abs(relative.getZ()));
           avg = avg.add(Vec3.atLowerCornerOf(abs));
        }
        avg = avg.scale((double) 1f /antennaParts.size());
        return avg;
    }
    public Vec3 getSize() {
        int largestX = 0;
        int largestY = 0;
        int largestZ = 0;
        int smallestX = 0;
        int smallestY = 0;
        int smallestZ = 0;
        for (BlockPos pos : antennaParts) {
            BlockPos relative = antennaActor.subtract(pos);
            if (relative.getX() > largestX)
                largestX = relative.getX();
            if (relative.getY() > largestY)
                largestY = relative.getY();
            if (relative.getZ() > largestZ)
                largestZ = relative.getZ();
            if (relative.getX() < smallestX)
                smallestX = relative.getX();
            if (relative.getY() < smallestY)
                smallestY = relative.getY();
            if (relative.getZ() < smallestZ)
                smallestZ = relative.getZ();
        }
        lowSize = new Vec3(smallestX,smallestY,smallestZ);
        highSize = new Vec3(largestX,largestY,largestZ);
        return new Vec3(largestX-smallestX,largestY-smallestY,largestZ-smallestZ);
    }
    public Vec3 getRelativeCenter() {
        Vec3 avg = new Vec3(0,0,0);
        for (BlockPos pos : antennaParts) {
            BlockPos relative = pos.subtract(antennaActor);
            avg = avg.add(Vec3.atLowerCornerOf(relative));
        }
        avg = avg.scale((double)1f /antennaParts.size());
        return avg;
    }
    public void updateDimensions() {
        relativeCenter = getRelativeCenter();
        avg = getAvg();
        size = getSize();
    }
}