package org.modogthedev.superposition.system.cable.rope_system;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class AnchorConstraint implements RopeConstraint {
    
    Direction direction;
    BlockPos anchorBlock;
    
    RopeNode node;
    
    public AnchorConstraint(Direction direction, BlockPos anchorBlock, RopeNode node) {
        this.direction = direction;
        this.anchorBlock = anchorBlock;
        this.node = node;
    }
    
    @Override
    public void iterateConstraint() {
        node.position = anchorBlock.getCenter().add(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.5f + 1/16f));
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public BlockPos getAnchorBlock() {
        return anchorBlock;
    }
    
}
