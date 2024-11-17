package org.modogthedev.superposition.system.cable;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.client.renderer.CableRenderer;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.SuperpositionConstants;
import org.modogthedev.superposition.util.Vec3LerpComponent;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Cable {
    private List<Point> points = new ArrayList<>();
    private HashMap<UUID, Integer> playerHoldingPointMap = new HashMap<>();
    private Level level;
    public float radius = SuperpositionConstants.cableRadius;
    public float elasticity = 0.9f;
    private Player playerHolding;
    private Color color;
    public Vec3 playerDraggedLastDelta = Vec3.ZERO;
    public int ticksSinceUpdate = 0;
    public int avgTicksSinceUpdate = 1;

    public Cable(Vec3 starAnchor, Vec3 endAnchor, int points, Level level, Color color) {
        addPoint(new Point(starAnchor));
        for (int i = 0; i < points; i++) {
            float delta = (float) i / points;
            addPoint(new Point(Mth.lerpVec3(starAnchor, endAnchor, delta)));
        }
        addPoint(new Point(endAnchor));
        this.level = level;
        this.color = color;
    }

    private Cable(List<Point> points, Level level, Color color) {
        this.points = points;
        this.level = level;
        this.color = color;
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

    public void followPlayer() {
        for (Point point : points) {
            point.grabbed = false;
        }
        for (UUID uuid : playerHoldingPointMap.keySet()) {
            Player player = level.getPlayerByUUID(uuid);
            if (player != null) {
                int index = playerHoldingPointMap.get(uuid);
                Point heldPoint = points.get(index);
                Point prevPoint;
                if (index > 0) {
                    prevPoint = points.get(index - 1);
                } else {
                    prevPoint = points.get(index + 1);
                }
                Vec3 playerOffset = player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(2));

                HitResult hitResult = level.clip(new ClipContext(player.getEyePosition(), playerOffset, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

                float maxLength = radius * points.size() * elasticity + radius * 2;
                float actualLength = 0;
                for (int i = 1; i < (points.size()); i++) {
                    Vec3 start = index == i - 1 ? hitResult.getLocation() : points.get(i - 1).position;
                    Vec3 end = index == i ? hitResult.getLocation() : points.get(i).position;
                    actualLength += (float) start.distanceTo(end);
                }
                Vec3 result = (Mth.lerpVec3(heldPoint.prevPosition, hitResult.getLocation(), net.minecraft.util.Mth.clamp(maxLength / actualLength, 0, 1)));
                heldPoint.grabbed = true;
                float stretch = (float) result.distanceTo(hitResult.getLocation());
                if (level.isClientSide && player.equals(Minecraft.getInstance().player)) {
                    CableRenderer.stretch = net.minecraft.util.Mth.clamp((float) (Math.log(stretch)/4f+1f),0,1);
                }
                if (stretch > 1.5f) {
                    if (level.isClientSide && player.equals(Minecraft.getInstance().player)) {
                        CableRenderer.detachPos = heldPoint.position;
                        CableRenderer.detachDelta = net.minecraft.util.Mth.clamp((float) (Math.log(stretch)/4f+1f),0,1);
                    }
                    CableManager.playerFinishDraggingCable(player, heldPoint.position);
                    return;
                }
                heldPoint.position = result;

//                addPoint(new Point(playerOffset));
            }
        }
    }

    public void updatePhysics() {
        ticksSinceUpdate++;
        updatePointsInBlocks();
        integrate();
        followPlayer();
        points.get(points.size() - 1).tempPos = points.get(points.size() - 1).position;
        update(false);
        update(true);
        for (Point point : points) {
            if (point.tempPos != null)
                point.position = Mth.lerpVec3(point.position, point.tempPos, 0.5f);
        }
        freeStuckPoints();
        updateCollisions();
        lerpPos();
        sendSignal();
    }


    private void freeStuckPoints() {
        for (int i = 1; i < points.size() - 1; i++) {
            Point point = points.get(i);
            Point lastPoint = points.get(i - 1);
            Point nextPoint = points.get(i + 1);
            float distanceBack = (float) point.getPosition().distanceTo(lastPoint.getPosition());
            float distanceForward = (float) point.getPosition().distanceTo(lastPoint.getPosition());
            if (distanceBack > SuperpositionConstants.cableRadius * 3) {
                if (distanceBack > distanceForward) {
                    point.setPosition(lastPoint.position);
                } else {
                    point.setPosition(nextPoint.position);
                }
                point.inBlock = true;
            }
            if (distanceForward > SuperpositionConstants.cableRadius * 3) {
                point.setPosition(nextPoint.position);
                point.inBlock = true;
            }
        }
    }

    private void sendSignal() {
        if (level != null && playerHolding == null) {
            BlockPos startPos = BlockPos.containing(points.get(0).getPosition());
            BlockPos endPos = BlockPos.containing(points.get(points.size() - 1).getPosition());
            if (level.isLoaded(startPos) && level.isLoaded(endPos)) {
                BlockEntity start = level.getBlockEntity(startPos);
                BlockEntity end = level.getBlockEntity(endPos);
                if (start instanceof SignalActorBlockEntity startSignalActor && end instanceof SignalActorBlockEntity endSignalActor) {
                    List<Signal> signalList = startSignalActor.getSignals();
                    if (signalList != null && !signalList.isEmpty()) {
                        endSignalActor.addSignals(signalList);
                    }
                }
            }
        }
    }

    private void lerpPos() {
        for (Point point : points) {
            if (point.lerpedPos != null) {
                point.position = point.lerpedPos.stepAndGather();
                if (point.lerpedPos.isComplete())
                    point.lerpedPos = null;
            }
        }
    }

    private void update(boolean isForwards) {
        if (isForwards) {
            for (int i = 1; i < (points.size()); i++) {
                Point point = points.get(i);
                if (point.inBlock)
                    continue;

                Point prevPoint = points.get(i - 1);

                point.forwardLength = (float) point.tempPos.subtract(prevPoint.tempPos).length();

                float distanceToMove = (float) (point.tempPos.distanceTo(prevPoint.tempPos) - radius) * (elasticity);
                Vec3 normal = (point.tempPos.subtract(prevPoint.tempPos)).normalize();
                point.tempPos = (point.tempPos.subtract(normal.scale(distanceToMove)));
            }
        } else {
            for (int i = (points.size() - 2); i >= 0; i--) {
                Point point = points.get(i);
                if (point.inBlock) {
                    point.tempPos = point.position;
                    continue;
                }

                Point prevPoint = points.get(i + 1);

                point.backwordsLength = (float) point.position.subtract(prevPoint.position).length();

                float distanceToMove = (float) (point.position.distanceTo(prevPoint.position) - radius) * (elasticity);
                Vec3 normal = (point.position.subtract(prevPoint.position)).normalize();
                point.tempPos = point.position;
                point.position = point.position.subtract(normal.scale(distanceToMove));
            }
        }
    }

    public void shrink() {
        if (points.size() > 4)
            points.remove(points.get(points.size() - 1));
    }

    private void updateCollisions() {
        for (int i = 0; i < (points.size()); i++) {
            Point point = points.get(i);
            if (point.lerpedPos == null && !point.inBlock && !point.grabbed) {
                Vec3 collision = Entity.collideBoundingBox((Entity) null, point.position.subtract(point.prevPosition), AABB.ofSize(point.prevPosition, radius, radius, radius), level, List.of());
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
    }

    private void integrate() {
        for (int i = 0; i < (points.size()); i++) {
            Point point = points.get(i);
            if (!point.inBlock && !point.grabbed) {
//            if (i < points.size() - 1) {
                Vec3 nextPosition = ((point.position.scale(2)).subtract(point.prevPosition)).add(new Vec3(0, -9.8, 0).scale(.05 * 0.05));
                Vec3 normal = (nextPosition.subtract(point.position));
                point.prevPosition = point.position;
                if (point.inContact)
                    point.position = point.position.add(normal.scale(0.7f));
                else
                    point.position = point.position.add(normal.scale(0.9f));
//            } else {
//            }
            } else {
                point.prevPosition = point.position;
            }
        }
    }

    public void setPlayerHolding(Player player) {
        addPoint(new Point(player.position()));
        addPlayerHoldingPoint(player.getUUID(), points.size() - 1);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(points.size());
        buf.writeInt(color.getRGB());
        for (Point point : points) {
            buf.writeDouble(point.position.x);
            buf.writeDouble(point.position.y);
            buf.writeDouble(point.position.z);
            buf.writeDouble(point.prevPosition.x);
            buf.writeDouble(point.prevPosition.y);
            buf.writeDouble(point.prevPosition.z);
        }
        buf.writeInt(playerHoldingPointMap.size());
        for (UUID uuid : playerHoldingPointMap.keySet()) {
            buf.writeUUID(uuid);
            buf.writeInt(playerHoldingPointMap.get(uuid));
        }
    }

    public void update(FriendlyByteBuf buf) {
        int size = buf.readInt();
        color = new Color(buf.readInt());
        List<Point> pointList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Point newPoint = new Point(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
            newPoint.setPrevPosition(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
            pointList.add(newPoint);
        }
        points = pointList;
        playerHoldingPointMap.clear();
        int playerHoldingMapSize = buf.readInt();
        for (int i = 0; i < playerHoldingMapSize; i++) {
            UUID uuid = buf.readUUID();
            int pointIndex = buf.readInt();
            playerHoldingPointMap.put(uuid, pointIndex);
        }
    }

    public HashMap<UUID, Integer> getPlayerHoldingPointMap() {
        return playerHoldingPointMap;
    }

    public static Cable fromBytes(FriendlyByteBuf buf, Level level) {
        int size = buf.readInt();
        Color color1 = new Color(buf.readInt());
        List<Point> pointList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Point newPoint = new Point(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
            newPoint.setPrevPosition(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
            pointList.add(newPoint);
        }
        Cable cable = new Cable(pointList, level, color1);
        int playerHoldingMapSize = buf.readInt();
        for (int i = 0; i < playerHoldingMapSize; i++) {
            UUID uuid = buf.readUUID();
            int pointIndex = buf.readInt();
            cable.addPlayerHoldingPoint(uuid, pointIndex);
        }
        return cable;
    }

    public void updateFromCable(Cable cable) {
        avgTicksSinceUpdate = ticksSinceUpdate;
        ticksSinceUpdate = 0;
        color = cable.color;
        if (points.size() >= cable.points.size() + 1) {
            points.subList(cable.points.size() + 1, points.size() + 1).clear();
        }
        if (cable.points.size() >= points.size() + 1) {
            points.addAll(cable.points.subList(points.size() - 1, cable.points.size() - 1)); //TODO: Test this
        }
        for (int i = 0; i < points.size(); i++) {
            points.get(i).setPosition(cable.points.get(i).getPosition());
            points.get(i).setPrevPosition(cable.points.get(i).getPrevPosition());
        }
        this.playerHoldingPointMap = cable.playerHoldingPointMap;
    }

    public void updatePointsInBlocks() {
        for (Point point : points) {
            point.ownedCable = this;
            updatePointInBlock(point);
        }
    }

    public void updatePointInBlock(Point point) {
        Vec3 vec3 = point.getPosition();
        BlockPos pos = BlockPos.containing(vec3);
        BlockHitResult blockHitResult = level.clip(new ClipContext(vec3.add(0.01f, 0.01f, 0.01f), vec3.subtract(0.01f, 0.01f, 0.01f), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        point.inBlock = blockHitResult.getType() == HitResult.Type.BLOCK;
    }

    public void addPlayerHoldingPoint(UUID playerUUID, int pointIndex) {
        playerHoldingPointMap.putIfAbsent(playerUUID, pointIndex);
    }

    public boolean hasPlayerHolding(UUID playerUUID) {
        return playerHoldingPointMap.containsKey(playerUUID);
    }

    public Pair<Point, Integer> getPlayerHeldPoint(UUID playerUUID) {
        if (playerHoldingPointMap.containsKey(playerUUID)) {
            int index = playerHoldingPointMap.get(playerUUID);
            if (points.size() > index)
                return new Pair<>(points.get(index), index);
        }
        return null;
    }

    public void addPointAtIndex(int index, Point point) {
        points.add(index, point);
    }

    public void replacePointAtIndex(int index, Point point) {
        points.set(index, point);
    }

    public int getPointIndex(Point point) {
        return points.indexOf(point);
    }

    public void stopPlayerDrag(UUID playerUUID) {
        playerHoldingPointMap.remove(playerUUID);
    }

    public Color getColor() {
        return color;
    }

    public static class Point {
        private Vec3 prevPosition;
        private Vec3 position;
        private Vec3 tempPos;
        private boolean inContact = false;
        private boolean inBlock;
        private boolean grabbed;
        private float forwardLength;
        private float backwordsLength;
        public Vec3LerpComponent lerpedPos = null;
        private Cable ownedCable;

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

        public Cable getOwnedCable() {
            return ownedCable;
        }
    }
}
