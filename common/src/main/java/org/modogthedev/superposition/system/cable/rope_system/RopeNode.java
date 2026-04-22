package org.modogthedev.superposition.system.cable.rope_system;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.compat.sable.SableCompat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RopeNode {

    RopeSimulation simulation;

    Vec3 renderPosition;
    Vec3 position;
    Vec3 prevPosition;
    Vec3 lastHoldGoalPos;

    CableSnapshotInterpolator interpolator;

    @Nullable
    AnchorConstraint anchor = null;

    List<Vec3> nextPositions = new ArrayList<>();

    public RopeNode(Vec3 position) {
        this.position = position;
        this.prevPosition = position;
        this.renderPosition = position;
        this.interpolator = new CableSnapshotInterpolator(JOMLConversion.toJOML(position));
    }

    public boolean isFixed() {
        return anchor != null;
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
        if (position.distanceToSqr(prevPosition) > 100) {
            position = prevPosition.add(position.subtract(prevPosition).normalize().scale(4f));
        }

        if (isFixed() || !level.isLoaded(BlockPos.containing(getPosition()))) {
            return;
        }
        Vec3 velocity = position.subtract(prevPosition);
        double initialYVelocity = velocity.y;

        Vec3 minBox = prevPosition.subtract(2 / 16f, 2 / 16f, 2 / 16f);
        Vec3 maxBox = prevPosition.add(2 / 16f, 2 / 16f, 2 / 16f);

        AABB collisionBox = new AABB(minBox, maxBox);

        if ((velocity.x != 0 || velocity.y != 0 || velocity.z != 0)) {
            Vec3 velocityVertical = new Vec3(0, velocity.y, 0);
            velocityVertical = Entity.collideBoundingBox(null, velocityVertical, collisionBox, level, List.of());
            velocityVertical = SableCompat.tryTransform(level,velocityVertical);

            collisionBox = collisionBox.move(velocityVertical);
            Vec3 velocityHorizontal = new Vec3(velocity.x, 0, velocity.z);
            velocityHorizontal = Entity.collideBoundingBox(null, velocityHorizontal, collisionBox, level, List.of());
            velocityHorizontal = SableCompat.tryTransform(level,velocityHorizontal);

            velocity = new Vec3(velocityHorizontal.x, velocityVertical.y, velocityHorizontal.z);
        }

        if (initialYVelocity < velocity.y && velocity.y <= 0) {
            velocity = new Vec3(velocity.x * 0.75, velocity.y, velocity.z * 0.75);
        }

        if (velocity.lengthSqr() < 0.00005) {
            velocity = velocity.scale(0.9f);
        }

        position = SableCompat.tryTransform(level,prevPosition.add(velocity));
    }

    public void read(FriendlyByteBuf buf, int gameTick) {
        setPosition(buf.readVec3());
        interpolator.receiveSnapshot(gameTick, JOMLConversion.toJOML(position));
        if (buf.readBoolean()) {
            setAnchor(buf.readEnum(Direction.class), buf.readBlockPos());
            if (buf.readBoolean()) {
                AnchorConstraint anchor = getAnchor();
                assert anchor != null : "Rope anchor is null after it was just set";
                anchor.setPort((String) buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8));
            }
        } else {
            removeAnchor();
        }
    }

    public RopeNode(FriendlyByteBuf buf, RopeSimulation simulation) {
        this.simulation = simulation;
        setPosition(buf.readVec3());
        if (buf.readBoolean()) {
            setAnchor(buf.readEnum(Direction.class), buf.readBlockPos());
            if (buf.readBoolean()) {
                AnchorConstraint anchor = getAnchor();
                assert anchor != null : "Rope anchor is null after it was just set";
                anchor.setPort((String) buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8));
            }
        }
        this.prevPosition = position;
        this.renderPosition = position;
        this.interpolator = new CableSnapshotInterpolator(JOMLConversion.toJOML(position));
    }

    public Vec3 getRenderPosition(float partialTicks) {
        return prevPosition.lerp(renderPosition, partialTicks);
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public void addNextPosition(Vec3 nextPosition) {
        this.nextPositions.add(nextPosition);
    }

    @Nullable
    public AnchorConstraint getAnchor() {
        return anchor;
    }

    public void setAnchor(Direction direction, BlockPos pos) {
        this.anchor = new AnchorConstraint(
                simulation,
                direction, pos, this,
                () -> simulation.getNode(simulation.getNodes().indexOf(this) - 1),
                () -> simulation.getNode(simulation.getNodes().indexOf(this) + 1),
                simulation.connectionWidth
        );
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

    public float calculateOverstretch() {
        return simulation.calculateOverstretch(this);
    }

    public Vec3 getPosition() {
        return position;
    }

    public void setLastDragGoalPos(Vec3 holdGoalPos) {
        this.lastHoldGoalPos = holdGoalPos;
    }

    public Vec3 getLastHoldGoalPos() {
        return lastHoldGoalPos;
    }
}
