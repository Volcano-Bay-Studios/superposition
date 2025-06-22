package org.modogthedev.superposition.system.cable.rope_system;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class AnchorConstraint implements RopeConstraint {

    Direction direction;
    BlockPos anchorBlock;

    RopeNode node;
    Supplier<RopeNode> adjacentPrev, adjacentNext;

    float width;

    public AnchorConstraint(Direction direction, BlockPos anchorBlock, RopeNode node, Supplier<RopeNode> adjacentPrev, Supplier<RopeNode> adjacentNext, float width) {
        this.direction = direction;
        this.anchorBlock = anchorBlock;
        this.node = node;
        this.adjacentPrev = adjacentPrev;
        this.adjacentNext = adjacentNext;
        this.width = width;
    }

    @Override
    public void applyConstraint() {
        node.position = anchorBlock.getCenter().add(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.5f + 1 / 16f));
        RopeNode prevNode = adjacentPrev.get();
        if (prevNode != null) {
            prevNode.position = BendConstraint.resolveAnchorBend(anchorBlock.getCenter(), node.position, prevNode.position, width);
        }
        RopeNode nextNode = adjacentNext.get();
        if (nextNode != null) {
            nextNode.position = BendConstraint.resolveAnchorBend(anchorBlock.getCenter(), node.position, nextNode.position, width);
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public BlockPos getAnchorBlock() {
        return anchorBlock;
    }

    @Override
    public double getStress() {
        return 0.0f;
    }

}
