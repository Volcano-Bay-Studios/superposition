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
    
    Vec3 renderPrevPosition;
    Vec3 prevPosition;
    Vec3 position;
    Vec3 playerDragPosition;

    @Nullable
    AnchorConstraint anchor = null;
    int anchorStress = 0;
    
    List<Vec3> nextPositions = new ArrayList<>();
    
    boolean fixed = false;
    
    public RopeNode(Vec3 position) {
        this.position = position;
        this.prevPosition = position;
        this.renderPrevPosition = position;
    }
    
    public boolean isFixed() {
        return fixed || anchor != null;
    }
    
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
    
    public void resolveWorldCollisions(Level level, boolean friction) {
        if (fixed || !level.isLoaded(BlockPos.containing(getPosition()))) return;
        Vec3 velocity = position.subtract(prevPosition);
        double initialYVelocity = velocity.y;
        
        Vec3 minBox = prevPosition.subtract(2/16f, 2/16f, 2/16f);
        Vec3 maxBox = prevPosition.add(2/16f, 2/16f, 2/16f);
        
        AABB collisionBox = new AABB(minBox, maxBox);
        
        if ((velocity.x != 0 || velocity.y != 0 || velocity.z != 0)) {
            Vec3 velocityVertical = new Vec3(0, velocity.y, 0);
            velocityVertical = Entity.collideBoundingBox(null, velocityVertical, collisionBox, level, List.of());
            
            collisionBox = collisionBox.move(velocityVertical);
            Vec3 velocityHorizontal = new Vec3(velocity.x, 0, velocity.z);
            velocityHorizontal = Entity.collideBoundingBox(null, velocityHorizontal, collisionBox, level, List.of());
            
            velocity = new Vec3(velocityHorizontal.x, velocityVertical.y, velocityHorizontal.z);
        }
        
        if (initialYVelocity < velocity.y && velocity.y <= 0) {
            if (friction)
                velocity = new Vec3(velocity.x * 0.5, velocity.y, velocity.z * 0.5);
        }
        
        if (velocity.lengthSqr() < 0.00005) {
            velocity = velocity.scale(0.9f);
        }
        
        position = prevPosition.add(velocity);
    }
    
    public Vec3 getPosition(float partialTicks) {
        if (simulation.isSleeping()) return position;
        return renderPrevPosition.lerp(position, partialTicks);
    }
    
    public Vec3 getPosition() {
        return position;
    }
    public Vec3 getPlayerDragPosition() {
        return playerDragPosition;
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
    
    public void setRenderPrevPosition(Vec3 renderPrevPosition) {
        this.renderPrevPosition = renderPrevPosition;
    }
    
    public void addNextPosition(Vec3 nextPosition) {
//        if (!isFixed()) this.position = position.lerp(nextPosition, 0.25);
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

    public void setPlayerDragPosition(Vec3 playerDragPosition) {
        this.playerDragPosition = playerDragPosition;
    }
    
    public Vec3 getPrevPosition() {
        return prevPosition;
    }

    public float calculateOverstretch() {
        return simulation.calculateOverstretch();
    }
    
    public Vec3 getRenderPrevPosition() {
        return renderPrevPosition;
    }
    
}
