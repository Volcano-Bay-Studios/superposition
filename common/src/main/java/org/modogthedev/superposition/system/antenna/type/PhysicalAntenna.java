package org.modogthedev.superposition.system.antenna.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaElement;
import org.modogthedev.superposition.system.antenna.classification.DipoleElement;
import org.modogthedev.superposition.system.antenna.classification.MonopoleElement;
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

    @Override
    public void updateTooltip(List<Component> tooltip) {
        super.updateTooltip(tooltip);
        if (!antennaElements.isEmpty()) {
            tooltip.add(Component.literal("Antenna Elements: "));
            for (AntennaElement antennaElement : antennaElements) {
                tooltip.add(Component.literal(antennaElement.getClassificationName() + " - " + SuperpositionMth.formatHz(antennaElement.getAntennaFrequency())));
            }
        } else {
            tooltip.add(Component.literal("No Antenna"));
        }
    }

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
        setPosition(new Vec3(antennaActor.getX(), antennaActor.getY() + highSize.y(), antennaActor.getZ()));
        classifyAntenna();
    }

    public void classifyAntenna() {
        antennaElements.clear();
        List<BlockPos> poleY = gatherAlongLine(antennaActor, Direction.Axis.Y);
        boolean foundSegment = false;
        List<BlockPos> ignoreList = new ArrayList<>();
        boolean firstSegment = searchSegment(poleY, ignoreList, Direction.Axis.Z);
        boolean secondSegment = searchSegment(poleY, ignoreList, Direction.Axis.X);
        if (firstSegment || secondSegment) {
                foundSegment = true;
            }
        if (!foundSegment && poleY.size() > 1) {
            antennaElements.add(new MonopoleElement(poleY.size(), SuperpositionMth.center(poleY)));
        }
    }

    /**
     * Adds antenna elements that do not branch into new elements
     *
     * @param segment
     * @param ignoreList
     * @param searchDirection
     * @return
     */
    protected boolean searchSegment(List<BlockPos> segment, List<BlockPos> ignoreList, Direction.Axis searchDirection) {
        boolean foundSegment = false;
        Direction.Axis opposingDirection = null;
        assert searchDirection != Direction.Axis.Y : "Tried to search segment with ambiguous opposing direction!";
        switch (searchDirection) {
            case X -> opposingDirection = Direction.Axis.Z;
            case Z -> opposingDirection = Direction.Axis.X;
        }
        for (BlockPos pos : segment) {
            if (!ignoreList.contains(pos)) {
                List<BlockPos> newSegment = gatherAlongLine(pos, searchDirection);
                if (newSegment.size() > 1) {
                    ignoreList.add(pos);
                    foundSegment = true;
                    boolean foundSecondSegment = searchSegment(newSegment, ignoreList, opposingDirection);
                    if (!foundSecondSegment) {
                        antennaElements.add(new DipoleElement(newSegment.size(), SuperpositionMth.center(newSegment)));
                    }
                }
            }
        }
        return foundSegment;
    }

    public List<BlockPos> gatherAlongLine(BlockPos startPos, Direction.Axis axis) {
        List<BlockPos> matching = new ArrayList<>();
        for (BlockPos antennaPart : antennaParts) {
            switch (axis) {
                case X: {
                    if (antennaPart.getY() == startPos.getY() && antennaPart.getZ() == startPos.getZ()) {
                        matching.add(antennaPart);
                    }
                    break;
                }
                case Y: {
                    if (antennaPart.getX() == startPos.getX() && antennaPart.getZ() == startPos.getZ()) {
                        matching.add(antennaPart);
                    }
                    break;
                }
                case Z: {
                    if (antennaPart.getX() == startPos.getX() && antennaPart.getY() == startPos.getY()) {
                        matching.add(antennaPart);
                    }
                    break;
                }
            }
        }
        return matching;
    }
}
