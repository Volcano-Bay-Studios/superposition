package org.modogthedev.superposition.system.cable.rope_system;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.compat.sable.SableCompat;
import org.modogthedev.superposition.core.SuperpositionConstants;

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
        RopeNode prevNode = adjacentPrev.get();
        RopeNode nextNode = adjacentNext.get();
        Vec3 anchorPosition = center.add(normal);
        if (prevNode != null) {
            if (prevNode.position.distanceTo(anchorPosition) > SuperpositionConstants.anchorSnapRange) {
                node.removeAnchor();
                return;
            }
        }
        if (nextNode != null) {
            if (nextNode.position.distanceTo(anchorPosition) > SuperpositionConstants.anchorSnapRange) {
                node.removeAnchor();
                return;
            }
        }
        node.position = anchorPosition;
        if (prevNode != null) {
            prevNode.position = BendConstraint.resolveAnchorBend(center, node.position, prevNode.position, width);
        }
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
