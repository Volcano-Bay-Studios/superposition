package org.modogthedev.superposition.system.cable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.SuperpositionConstants;
import org.modogthedev.superposition.util.Vec3LerpComponent;

import java.util.ArrayList;
import java.util.List;

public class Cable {
    private List<Point> points = new ArrayList<>();
    private final Level level;
    public float radius = SuperpositionConstants.cableRadius;
    public float elasticity = 0.99f;
    private Player playerHolding;
    public Vec3 playerDraggedLastDelta = Vec3.ZERO;

    public Cable(Vec3 starAnchor, Vec3 endAnchor, int points, Level level) {
        addPoint(new Point(starAnchor));
        for (int i = 0; i < points; i++) {
            float delta = (float) i / points;
            addPoint(new Point(Mth.lerpVec3(starAnchor, endAnchor, delta)));
        }
        addPoint(new Point(endAnchor));
        this.level = level;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void debugDraw() {
        for (Point point : points) {
            assert Minecraft.getInstance().level != null;
            Minecraft.getInstance().level.addParticle(ParticleTypes.ELECTRIC_SPARK, point.position.x, point.position.y, point.position.z, 0, 0, 0);
        }
    }

    private void followPlayer() {
        if (playerHolding != null) {
            Point heldPoint = points.get(points.size() - 1);
            Vec3 playerOffset = playerHolding.getRopeHoldPosition(0);
            Vec3 moveVec = playerOffset.subtract(heldPoint.position);
            if (heldPoint.position.distanceTo(playerOffset) < 10) {
                heldPoint.position = heldPoint.position.add(moveVec.scale(1));
            } else {
                CableManager.playerFinishDraggingCable(playerHolding, heldPoint.position);
            }
//            if (radius*1.5f < points.get(points.size() - 1).position.subtract(points.get(points.size() - 2).position).length())
//                addPoint(new Point(playerOffset));
        }
    }

    public void updatePhysics() {
        integrate();
        followPlayer();
        update(false);
        update(true);
        updateCollisions();
        lerpPos();
        sendSignal();
    }

    private void sendSignal() {
        if (level != null && playerHolding == null) {
            BlockPos startPos = BlockPos.containing(points.get(0).getPosition());
            BlockPos endPos = BlockPos.containing(points.get(points.size() - 1).getPosition());
            if (level.isLoaded(startPos) && level.isLoaded(endPos)) {
                BlockEntity start = level.getBlockEntity(startPos);
                BlockEntity end = level.getBlockEntity(endPos);
                if (start instanceof SignalActorBlockEntity startSignalActor && end instanceof  SignalActorBlockEntity endSignalActor) {
                    List<Signal> signalList = startSignalActor.getSignals();
                    if (signalList != null && !signalList.isEmpty()) {
                        endSignalActor.putSignalList(new Object(),signalList);
                    }
                }
            }
        }
    }

    private void lerpPos() {
        Point lastPoint = points.get(points.size() - 1);
        if (lastPoint.lerpedPos != null) {
            lastPoint.position = lastPoint.lerpedPos.stepAndGather();
            if (lastPoint.lerpedPos.isComplete())
                lastPoint.lerpedPos = null;
        }
    }

    private void update(boolean isForwards) {
        if (isForwards) {
            for (int i = 1; i < (points.size() - 1); i++) {
                Point point = points.get(i);
                Point prevPoint = points.get(i - 1);

                point.forwardLength = (float) point.position.subtract(prevPoint.position).length();

                float distanceToMove = (float) (point.position.distanceTo(prevPoint.position) - radius) * elasticity;
                Vec3 normal = (point.position.subtract(prevPoint.position)).normalize();
                point.setPosition(point.position.subtract(normal.scale(distanceToMove)));
            }
        } else {
            for (int i = (points.size() - 2); i > 1; i--) {
                Point point = points.get(i);
                Point prevPoint = points.get(i + 1);

                point.backwordsLength = (float) point.position.subtract(prevPoint.position).length();

                float distanceToMove = (float) (point.position.distanceTo(prevPoint.position) - radius) * elasticity;
                Vec3 normal = (point.position.subtract(prevPoint.position)).normalize();
                point.setPosition(point.position.subtract(normal.scale(distanceToMove)));
            }
        }
    }

    public void shrink() {
        if (points.size() > 4)
            points.remove(points.get(points.size() - 1));
    }

    private void updateCollisions() {
        for (int i = 1; i < (points.size() - 1); i++) {
            Point point = points.get(i);
            Vec3 collision = Entity.collideBoundingBox((Entity) null, point.position.subtract(point.prevPosition), AABB.ofSize(point.prevPosition.subtract(0, 0, 0), radius, radius, radius), level, List.of());
            Vec3 velocity = point.position.subtract(point.prevPosition);
            point.setInContact(false);
            if (collision.subtract(velocity).length() != 0) {
                point.setInContact(true);
                if (level.isClientSide) {
                    assert Minecraft.getInstance().level != null;
//                    Minecraft.getInstance().level.addParticle(ParticleTypes.WAX_ON, point.position.x, point.position.y, point.position.z, 0, 0, 0);
                }
            }
            point.position = point.position.add(collision.subtract(velocity));
//            }
        }
    }

    private void integrate() {
        for (int i = 1; i < (points.size()); i++) {
            Point point = points.get(i);
            if (i < points.size() - 1) {
                Vec3 nextPosition = ((point.position.scale(2)).subtract(point.prevPosition)).add(new Vec3(0, -9.8, 0).scale(.05 * 0.05));
                Vec3 normal = (nextPosition.subtract(point.position));
                point.prevPosition = point.position;
                if (point.inContact)
                    point.position = point.position.add(normal.scale(0.7f));
                else
                    point.position = point.position.add(normal.scale(0.9f));
            } else {
                point.prevPosition = point.position;
            }
        }
    }

    public void setPlayerHolding(Player player) {
        this.playerHolding = player;
    }

    public List<Point> getPoints() {
        return points;
    }


    public static class Point {
        private Vec3 prevPosition;
        private Vec3 position;
        private boolean inContact = false;
        private float forwardLength;
        private float backwordsLength;
        public Vec3LerpComponent lerpedPos = null;

        public Point(Vec3 position) {
            this.position = position;
            this.prevPosition = position;
        }

        public Vec3 getPrevPosition() {
            return prevPosition;
        }

        public Vec3 getPosition() {
            return position;
        }

        public boolean getInContact() {
            return inContact;
        }

        public void setPosition(Vec3 vec3) {
            this.position = vec3;
        }

        public float getLength() {
            return Math.max(forwardLength, backwordsLength);
        }

        public void setPrevPosition(Vec3 vec3) {
            this.prevPosition = vec3;
        }

        public void setInContact(boolean inContact) {
            this.inContact = inContact;
        }
    }
}
