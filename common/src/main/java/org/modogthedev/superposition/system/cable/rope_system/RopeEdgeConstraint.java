package org.modogthedev.superposition.system.cable.rope_system;

import net.minecraft.world.phys.Vec3;

public class RopeEdgeConstraint implements RopeConstraint, BendConstraint {
    
    RopeNode middle;
    RopeNode to;
    
    float width;
    
    public RopeEdgeConstraint(RopeNode middle, RopeNode to, float width) {
        this.middle = middle;
        this.to = to;
        this.width = width;
    }
    
    @Override
    public void iterateConstraint() {
        if (middle.anchor != null) {
            to.addNextPosition(BendConstraint.resolveAnchorBend(
                middle.anchor.getAnchorBlock().getCenter()
                    .add(Vec3.atLowerCornerOf(middle.anchor.direction.getNormal()).scale(6/16f)),
                middle.position, to.position, width
            ));
        } else {
            to.addNextPosition(BendConstraint.resolveSinglePair(middle.position, to.position, width));
        }
    }
    
}
