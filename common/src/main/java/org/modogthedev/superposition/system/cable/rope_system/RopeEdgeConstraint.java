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
    public void applyConstraint() {
        if (middle.anchor != null) {
            to.addNextPosition(BendConstraint.resolveAnchorBend(
                middle.anchor.getAnchorBlock().getCenter()
                    .add(Vec3.atLowerCornerOf(middle.anchor.direction.getNormal()).scale(6/16f)),
                middle.position, to.position, width
            ));
        } else {
            Vec3 newPos = BendConstraint.resolveSinglePair(middle.position, to.position, width);
            to.addNextPosition(newPos);
        }
    }
    
    @Override
    public double getStress() {
        return to.position.distanceTo(middle.position);
    }
}
