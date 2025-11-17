package org.modogthedev.superposition.system.antenna.type;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.ArrayList;
import java.util.List;

public class PhysicalAntenna extends Antenna {
    public List<BlockPos> antennaParts = new ArrayList<>();
    public Vector3d avg = new Vector3d();
    public Vector3d size = new Vector3d();
    public Vector3d lowSize = new Vector3d();
    public Vector3d highSize = new Vector3d();
    public Vector3d relativeCenter = new Vector3d();
    public PhysicalAntenna(List<BlockPos> antennaParts, BlockPos antennaActor, Level level) {
        super(antennaActor, level);
        this.antennaParts = antennaParts;
    }

    public Vector3d getAvg(Vector3d store) {
        store.set(0.0);
        for (BlockPos pos : this.antennaParts) {
            store.add(Math.abs(pos.getX() - this.antennaActor.getX()), Math.abs(pos.getY() - this.antennaActor.getY()), Math.abs(pos.getZ() - this.antennaActor.getZ()));
        }
        return store.div(this.antennaParts.size());
    }

    private void calculateSize() {
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
        this.calculateSize();
        setFrequency(SuperpositionMth.antennaSizeToHz(antennaParts.size()));
        setPosition(new Vec3(antennaActor.getX(), antennaActor.getY()+highSize.y(), antennaActor.getZ()));
    }
    public List<BlockPos> getAntennaParts() {
        return antennaParts;
    }
}
