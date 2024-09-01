package org.modogthedev.superposition.system.cable;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class Cable {
    private List<Point> points = new ArrayList<>();
    private final Level level;
    public float radius = 0.5f;
    public float elasticity = 0.8f;
    private Player playerHolding;

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
            Vec3 playerOffset = playerHolding.getRopeHoldPosition(0).add(0, 0.5, 0);
            points.get(points.size() - 1).position = playerOffset;
//            if (radius*1.5f < points.get(points.size() - 1).position.subtract(points.get(points.size() - 2).position).length())
//                addPoint(new Point(playerOffset));
        }
    }

    public void updatePhysics() {
        followPlayer();
        integrate();
        update(true);
        update(false);
        updateCollisions();
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
        for (int i = 1; i < (points.size() - 1); i++) {
            Point point = points.get(i);
            Vec3 nextPosition = ((point.position.scale(2)).subtract(point.prevPosition)).add(new Vec3(0, -9.8, 0).scale(.05 * 0.05));
            Vec3 normal = (nextPosition.subtract(point.position));
            point.prevPosition = point.position;
            if (point.inContact)
                point.position = point.position.add(normal.scale(0.7f));
            else
                point.position = point.position.add(normal.scale(0.99f));
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
            if (forwardLength > backwordsLength)
                return forwardLength;
            else
                return backwordsLength;
        }

        public void setPrevPosition(Vec3 vec3) {
            this.prevPosition = vec3;
        }

        public void setInContact(boolean inContact) {
            this.inContact = inContact;
        }
    }
}
