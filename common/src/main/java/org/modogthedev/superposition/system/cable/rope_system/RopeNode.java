package org.modogthedev.superposition.system.cable.rope_system;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RopeNode {
    
    RopeSimulation simulation;
    
    Vec3 prevPosition;
    Vec3 position;
    
    @Nullable
    AnchorConstraint anchor = null;
    
    List<Vec3> nextPositions = new ArrayList<>();
    
    boolean fixed = false;
    boolean fixedByPlayer = false;
    
    public RopeNode(Vec3 position) {
        this.position = position;
        this.prevPosition = position;
    }
    
    public boolean isFixed() {
        return fixed || fixedByPlayer || anchor != null;
    }
    
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
    
    public void setFixedByPlayer(boolean fixedByPlayer) {
        this.fixedByPlayer = fixedByPlayer;
    }
    
    public void simulatePhysics() {
        Vec3 velocity = Vec3.ZERO;
        if (!fixed) {
            velocity = position.subtract(prevPosition);
            
            velocity = velocity.add(0, -0.1, 0);
            velocity = velocity.scale(0.9);
        }
        
        prevPosition = position;
        position = position.add(velocity);
    }
    
    public void resolveWorldCollisions(Level level) {
        if (fixed) return;
        Vec3 velocity = position.subtract(prevPosition);
        double initialYVelocity = velocity.y;
        
        Vec3 minBox = prevPosition.subtract(0.1, 0.1, 0.1);
        Vec3 maxBox = prevPosition.add(0.1, 0.1, 0.1);
        
        AABB collisionBox = new AABB(minBox, maxBox);
        
        if ((velocity.x != 0 || velocity.y != 0 || velocity.z != 0))
            velocity = Entity.collideBoundingBox(null, velocity, collisionBox, level, List.of());
        
        if (initialYVelocity < velocity.y && velocity.y <= 0) {
            velocity = new Vec3(velocity.x * 0.1, velocity.y, velocity.z * 0.1);
        }
        
        position = prevPosition.add(velocity);
    }
    
    public Vec3 getPosition(float partialTicks) {
        return prevPosition.lerp(position, partialTicks);
    }
    
    public Vec3 getPosition() {
        return position;
    }
    
    public void setPosition(Vec3 position) {
        this.position = position;
    }
    
    public List<Vec3> getNextPositions() {
        return nextPositions;
    }
    
    public void setNextPositions(List<Vec3> nextPositions) {
        this.nextPositions = nextPositions;
    }
    
    public void addNextPosition(Vec3 nextPosition) {
        this.nextPositions.add(nextPosition);
    }
    
    public void applyNextPositions() {
        if (nextPositions.isEmpty() || isFixed()) return;
        
        Vec3 current = position;
        int count = 1;
        for (Vec3 pos : nextPositions) {
            current = current.add(pos);
            count++;
        }
        
        this.position = current.scale(1f / count);
        setNextPositions(new ArrayList<>());
    }
    
    @Nullable
    public AnchorConstraint getAnchor() {
        return anchor;
    }
    
    public void setAnchor(Direction direction, BlockPos pos) {
        this.anchor = new AnchorConstraint(direction, pos, this);
        this.simulation.addConstraint(this.anchor);
    }
    
    public void removeAnchor() {
        this.simulation.removeConstraint(this.anchor);
        this.anchor = null;
    }
    
    public void setPrevPosition(Vec3 prevPosition) {
        this.prevPosition = prevPosition;
    }
    
    public Vec3 getPrevPosition() {
        return prevPosition;
    }
    
}
