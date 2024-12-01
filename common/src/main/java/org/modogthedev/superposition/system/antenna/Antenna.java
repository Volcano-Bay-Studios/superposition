package org.modogthedev.superposition.system.antenna;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;

public class Antenna {
    public Level level;
    public List<BlockPos> antennaParts = new ArrayList<>();
    public List<Signal> signals = new ArrayList<>();
    public BlockPos antennaActor;
    public boolean reading;
    public Vector3d avg;
    public Vector3d size;
    public Vector3d lowSize;
    public Vector3d highSize;
    public Vector3d relativeCenter;

    public Antenna(List<BlockPos> antennaParts, BlockPos antennaActor, Level level) {
        this.antennaParts = antennaParts;
        this.antennaActor = antennaActor;
        this.level = level;
    }

    public boolean isPos(BlockPos pos) {
        return antennaActor.equals(pos);
    }

    public Vector3d getAvg(Vector3d store) {
        store.set(0.0);
        for (BlockPos pos : this.antennaParts) {
            store.add(Math.abs(pos.getX() - this.antennaActor.getX()), Math.abs(pos.getY() - this.antennaActor.getY()), Math.abs(pos.getZ() - this.antennaActor.getZ()));
        }
        return store.div(this.antennaParts.size());
    }

    private void getSize() {
        int largestX = 0;
        int largestY = 0;
        int largestZ = 0;
        int smallestX = 0;
        int smallestY = 0;
        int smallestZ = 0;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (BlockPos part : this.antennaParts) {
            BlockPos relative = pos.setWithOffset(this.antennaActor, -part.getX(), -part.getY(), -part.getZ());
            if (relative.getX() > largestX) {
                largestX = relative.getX();
            }
            if (relative.getY() > largestY) {
                largestY = relative.getY();
            }
            if (relative.getZ() > largestZ) {
                largestZ = relative.getZ();
            }
            if (relative.getX() < smallestX) {
                smallestX = relative.getX();
            }
            if (relative.getY() < smallestY) {
                smallestY = relative.getY();
            }
            if (relative.getZ() < smallestZ) {
                smallestZ = relative.getZ();
            }
        }
        this.lowSize.set(smallestX, smallestY, smallestZ);
        this.highSize.set(largestX, largestY, largestZ);
        this.size.set(largestX - smallestX, largestY - smallestY, largestZ - smallestZ);
    }

    public Vector3d getRelativeCenter(Vector3d store) {
        store.set(0.0);
        for (BlockPos pos : this.antennaParts) {
            store.add(pos.getX() - this.antennaActor.getX(), pos.getY() - this.antennaActor.getY(), pos.getZ() - this.antennaActor.getZ());
        }
        return store.div(this.antennaParts.size());
    }

    public void updateDimensions() {
        this.getRelativeCenter(this.relativeCenter);
        this.getAvg(this.avg);
        this.getSize();
    }
}