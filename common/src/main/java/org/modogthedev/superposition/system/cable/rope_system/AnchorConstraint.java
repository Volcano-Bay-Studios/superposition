package org.modogthedev.superposition.system.cable.rope_system;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.compat.sable.SableCompat;

import java.util.function.Supplier;

public class AnchorConstraint implements RopeConstraint {

    Direction direction;
    BlockPos anchorBlock;
    RopeSimulation simulation;
    String port = null;

    RopeNode node;
    Supplier<RopeNode> adjacentPrev, adjacentNext;

    float width;

    public AnchorConstraint(RopeSimulation simulation, Direction direction, BlockPos anchorBlock, RopeNode node, Supplier<RopeNode> adjacentPrev, Supplier<RopeNode> adjacentNext, float width) {
        this.simulation = simulation;
        this.direction = direction;
        this.anchorBlock = anchorBlock;
        this.node = node;
        this.adjacentPrev = adjacentPrev;
        this.adjacentNext = adjacentNext;
        this.width = width;
    }

    @Override
    public void applyConstraint() {
        Vec3 center = SableCompat.tryTransform(simulation.getLevel(),anchorBlock.getCenter());
        Vec3 normal = SableCompat.transformNormal(simulation.getLevel(), anchorBlock.getCenter() ,Vec3.atLowerCornerOf(getDirection().getNormal())).scale(0.5f + 1 / 16f);
        node.position =  center.add(normal);
        RopeNode prevNode = adjacentPrev.get();
        if (prevNode != null) {
            prevNode.position = BendConstraint.resolveAnchorBend(center, node.position, prevNode.position, width);
        }
        RopeNode nextNode = adjacentNext.get();
        if (nextNode != null) {
            nextNode.position = BendConstraint.resolveAnchorBend(center, node.position, nextNode.position, width);
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public BlockPos getAnchorBlock() {
        return anchorBlock;
    }

    public @Nullable String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public double getStress() {
        return 0.0f;
    }

}
