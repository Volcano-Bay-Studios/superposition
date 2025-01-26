package org.modogthedev.superposition.system.cable.rope_system;

public class RopeMiddleConstraint implements RopeConstraint, BendConstraint {
    
    RopeNode from;
    RopeNode middle;
    RopeNode to;
    
    float width;
    
    public RopeMiddleConstraint(RopeNode from, RopeNode middle, RopeNode to, float width) {
        this.from = from;
        this.middle = middle;
        this.to = to;
        this.width = width;
    }
    
    @Override
    public void iterateConstraint() {
        
        to.addNextPosition(BendConstraint.resolve(from.position, middle.position, to.position, width));
        from.addNextPosition(BendConstraint.resolve(to.position, middle.position, from.position, width));
        
    }
    
}
