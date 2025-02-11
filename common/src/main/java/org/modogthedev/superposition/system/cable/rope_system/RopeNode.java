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
    Vec3 tempPosition;

    @Nullable
    AnchorConstraint anchor = null;

    List<Vec3> nextPositions = new ArrayList<>();

    boolean fixed = false;

    public RopeNode(Vec3 position) {
        this.position = position;
        this.prevPosition = position;
    }

    public boolean isFixed() {
        return fixed || anchor != null;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public RopeNode getNext() {
        int i = simulation.nodes.indexOf(this);
        if (i < simulation.nodes.size() - 1) {
            return simulation.nodes.get(i + 1);
        }
        return null;
    }

    public RopeNode getLast() {
        int i = simulation.nodes.indexOf(this);
        if (i > 0) {
            return simulation.nodes.get(i - 1);
        }
        return null;
    }

    public void resolveWorldCollisions(Level level) {
        if (fixed || !level.isLoaded(BlockPos.containing(getPosition()))) return;
        Vec3 velocity = position.subtract(prevPosition);
        double initialYVelocity = velocity.y;

        Vec3 minBox = prevPosition.subtract(2 / 16f, 2 / 16f, 2 / 16f);
        Vec3 maxBox = prevPosition.add(2 / 16f, 2 / 16f, 2 / 16f);

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
            velocity = new Vec3(velocity.x * 0.75, velocity.y, velocity.z * 0.75);
        }

        if (velocity.lengthSqr() < 0.00005) {
            velocity = velocity.scale(0.9f);
        }

        position = prevPosition.add(velocity);
    }

    public Vec3 getPosition(float partialTicks) {
        if (simulation.isSleeping()) return position;
        return prevPosition.lerp(position, partialTicks);
    }

    public Vec3 getPosition() {
        return position;
    }

    public Vec3 getTempPosition() {
        return tempPosition;
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
        if (!isFixed()) this.position = position.lerp(nextPosition, 0.25);
//        this.nextPositions.add(nextPosition);
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

    public void setTempPosition(Vec3 tempPosition) {
        this.tempPosition = tempPosition;
    }

    public Vec3 getPrevPosition() {
        return prevPosition;
    }

    public float calculateOverstretch() {
        return simulation.calculateOverstretch();
    }

}
