package org.modogthedev.superposition.system.cable;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Vector3d;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.client.renderer.CableRenderer;
import org.modogthedev.superposition.core.SuperpositionCards;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.Vec3LerpComponent;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cable {

    private final UUID id;
    private List<Point> points = new ArrayList<>();
    private Int2IntMap playerHoldingPointMap = new Int2IntArrayMap();
    private Level level;
    public float radius = SuperpositionConstants.cableRadius;
    public float elasticity = 0.9f;
    private Player playerHolding;
    private Color color;
    public Vec3 playerDraggedLastDelta = Vec3.ZERO;
    public int ticksSinceUpdate = 0;
    public int sleepTimer = 20;
    private float lastMovement;
    private float averageMovement = 0;

    public Cable(UUID id, Vec3 starAnchor, Vec3 endAnchor, int points, Level level, Color color) {
        this.id = id;
        this.addPoint(new Point(starAnchor));
        for (int i = 0; i < points; i++) {
            float delta = (float) i / points;
            this.addPoint(new Point(Mth.lerpVec3(starAnchor, endAnchor, delta)));
        }
        this.addPoint(new Point(endAnchor));
        this.level = level;
        this.color = color;
    }

    private Cable(UUID id, List<Point> points, Level level, Color color) {
        this.id = id;
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
        for (int id : playerHoldingPointMap.keySet()) {
            Entity entity = level.getEntity(id);
            if (entity instanceof Player player) {
                int index = playerHoldingPointMap.get(id);
                Point heldPoint = points.get(index);
                Point prevPoint;
                if (index > 0) {
                    prevPoint = points.get(index - 1);
                } else {
                    prevPoint = points.get(index + 1);
                }
                Vec3 playerOffset = player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(2));

                HitResult hitResult = level.clip(new ClipContext(player.getEyePosition(), playerOffset, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

                float maxLength = this.getMaxLength();
                float actualLength = 0;
                for (int i = 1; i < (points.size()); i++) {
                    Vec3 start = index == i - 1 ? hitResult.getLocation() : points.get(i - 1).position;
                    Vec3 end = index == i ? hitResult.getLocation() : points.get(i).position;
                    actualLength += (float) start.distanceTo(end);
                }
                boolean isEndPoint = heldPoint.equals(points.getFirst()) || heldPoint.equals(points.getLast());
                Vec3 result = (Mth.lerpVec3(heldPoint.prevPosition, hitResult.getLocation(), net.minecraft.util.Mth.clamp((maxLength + (isEndPoint ? 0 : 1)) / (actualLength), 0, 1)));
                heldPoint.grabbed = true;
                float stretch = (float) result.distanceTo(hitResult.getLocation());
                if (level.isClientSide && player.equals(Minecraft.getInstance().player)) {
                    CableRenderer.stretch = net.minecraft.util.Mth.clamp((float) (Math.log(stretch) / 4f + 1f), 0, 1);
                }
                if (stretch > 1.5f) {
                    if (level.isClientSide && player.equals(Minecraft.getInstance().player)) {
                        CableRenderer.detachPos = heldPoint.position;
                        CableRenderer.detachDelta = net.minecraft.util.Mth.clamp((float) (Math.log(stretch) / 4f + 1f), 0, 1);
                    }
                    CableManager.playerFinishDraggingCable(player, BlockPos.containing(heldPoint.position), null);
                    return;
                }
                heldPoint.position = result;

//                addPoint(new Point(playerOffset));
            }
        }
    }

    public float getMaxLength() {
        return radius * points.size() * elasticity + radius * 2;
    }

    public void updatePhysics() {
        ticksSinceUpdate++;
        if (!playerHoldingPointMap.isEmpty()) {
            sleepTimer = 60;
        }
        if (sleepTimer > 0) {
            this.updatePointsInBlocks();
            lastMovement = 0;
            this.integrate();
            averageMovement = (lastMovement + (averageMovement*19)) / 20f;
            if (averageMovement > 0.8f) {
                sleepTimer = 60;
            } else {
                sleepTimer--;
            }
            this.followPlayer();
            points.getLast().tempPos = points.getLast().position;
            this.update(false);
            this.update(true);
            for (Point point : points) {
                if (point.tempPos != null) {
                    point.position = Mth.lerpVec3(point.position, point.tempPos, 0.5f);
                }
            }
            this.freeStuckPoints();
            this.updateCollisions();
            this.lerpPos();
        } else {
            for (Point point : points) {
                point.setPrevPosition(point.position);
            }
        }
        this.sendSignal();
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
            BlockPos startPos = BlockPos.containing(points.getFirst().getPosition());
            BlockPos endPos = BlockPos.containing(points.getLast().getPosition());
            if (level.isLoaded(startPos) && level.isLoaded(endPos)) {
                BlockEntity start = level.getBlockEntity(startPos);
                BlockEntity end = level.getBlockEntity(endPos);
                if (points.getFirst().attachedFace == Direction.UP && start instanceof ComputerBlockEntity cbe) {
                    Card card = cbe.getCard();
                    if (card != null && end instanceof SignalActorBlockEntity signalActorBlockEntity) { // Tells a periphreal what card is being used
                        float frequency = 0;
                        if (!cbe.getSignals().isEmpty())
                            frequency = cbe.getSignal().getFrequency();
                        BlockPos blockPos = cbe.getBlockPos();
                        Vector3d pos = new Vector3d(blockPos.getX(),blockPos.getY(),blockPos.getZ());
                        Signal periphrealSignal = new Signal(pos, level, frequency, 1, frequency / 100000);
                        periphrealSignal.encode(SuperpositionCards.CARDS.asVanillaRegistry().getId(SuperpositionCards.CARDS.asVanillaRegistry().get(card.getSelfReference()))); // Encode the id of the card for the analyser
                        signalActorBlockEntity.putSignal(periphrealSignal);
                    }
                } else if (points.getLast().attachedFace == Direction.UP && end instanceof ComputerBlockEntity cbe) { // Applies top port cable signal as periphreal
                    Card card = cbe.getCard();
                    if (card != null && start instanceof SignalActorBlockEntity signalActorBlockEntity) {
                        cbe.acceptPeriphrealSignal(signalActorBlockEntity.getSignal());
                    }
                } else if (start instanceof SignalActorBlockEntity startSignalActor && end instanceof SignalActorBlockEntity endSignalActor) {
                    List<Signal> signalList = startSignalActor.getSignals();
                    if (signalList != null && !signalList.isEmpty() && startSignalActor != endSignalActor) {
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
                if (point.lerpedPos.isComplete()) {
                    point.lerpedPos = null;
                }
            }
        }
    }

    private void update(boolean isForwards) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        if (isForwards) {
            for (int i = 1; i < points.size(); i++) {
                Point point = points.get(i);
                blockPos.set(point.getPosition().x,point.getPosition().y,point.getPosition().z);
                if (point.getAttachedFace() != null || !level.isLoaded(blockPos)) {
                    continue;
                }

                Point prevPoint = points.get(i - 1);

                point.forwardLength = (float) point.tempPos.subtract(prevPoint.tempPos).length();

                float distanceToMove = (float) (point.tempPos.distanceTo(prevPoint.tempPos) - radius) * (elasticity);
                Vec3 normal = (point.tempPos.subtract(prevPoint.tempPos)).normalize();
                point.tempPos = (point.tempPos.subtract(normal.scale(distanceToMove)));
            }
        } else {
            for (int i = (points.size() - 2); i >= 0; i--) {
                Point point = points.get(i);
                blockPos.set(point.getPosition().x,point.getPosition().y,point.getPosition().z);
                if (point.getAttachedFace() != null || !level.isLoaded(blockPos)) {
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
        if (points.size() > 4) {
            points.remove(points.getLast());
        }
    }

    private void updateCollisions() {
        for (Point point : points) {
            if (point.lerpedPos == null && !point.inBlock && !point.grabbed) {
                Vec3 collision = Entity.collideBoundingBox(null, point.position.subtract(point.prevPosition), AABB.ofSize(point.prevPosition, radius, radius, radius), level, List.of());
                Vec3 velocity = point.position.subtract(point.prevPosition);
                point.setInContact(false);
                if (collision.subtract(velocity).length() != 0) {
                    point.setInContact(true);
                    assert !level.isClientSide || Minecraft.getInstance().level != null;
                }
                point.position = point.position.add(collision.subtract(velocity));
//            }
            }
        }
    }

    private void integrate() {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (Point point : points) {
            blockPos.set(point.getPosition().x,point.getPosition().y,point.getPosition().z);
            if (!point.inBlock && !point.grabbed && level.isLoaded(blockPos)) {
                Vec3 nextPosition = ((point.position.scale(2)).subtract(point.prevPosition)).add(new Vec3(0, -13.8, 0).scale(.05 * 0.05));
                Vec3 offset = (nextPosition.subtract(point.position));
                lastMovement += (float) offset.length();
                point.prevPosition = point.position;
                if (point.inContact) {
                    point.position = point.position.add(offset.scale(0.7f));
                } else {
                    point.position = point.position.add(offset.scale(sleepTimer == 60 ? 0.999f : Mth.map(sleepTimer,60,0,0.999f,0.9f)));
                }
            } else {
                point.prevPosition = point.position;
            }
        }
    }

    public void setPlayerHolding(Player player) {
        this.addPoint(new Point(player.position()));
        this.addPlayerHoldingPoint(player.getId(), points.size() - 1);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(color.getRGB());
        buf.writeVarInt(points.size());
        for (Point point : points) {
            buf.writeVec3(point.position);
            buf.writeVec3(point.prevPosition);
            boolean hasFace = point.attachedPoint != null && point.attachedFace != null;
            buf.writeBoolean(hasFace);
            if (hasFace) {
                buf.writeBlockPos(point.attachedPoint);
                buf.writeEnum(point.attachedFace);
            }
        }
        buf.writeVarInt(playerHoldingPointMap.size());
        for (Int2IntMap.Entry entry : playerHoldingPointMap.int2IntEntrySet()) {
            buf.writeVarInt(entry.getIntKey());
            buf.writeVarInt(entry.getIntValue());
        }
    }

    public static Cable fromBytes(UUID id, FriendlyByteBuf buf, Level level) {
        Color color1 = new Color(buf.readInt());
        int size = buf.readVarInt();
        List<Point> pointList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Point newPoint = new Point(buf.readVec3());
            newPoint.setPrevPosition(buf.readVec3());
            if (buf.readBoolean()) {
                newPoint.setAnchor(buf.readBlockPos(), buf.readEnum(Direction.class));
            }
            pointList.add(newPoint);
        }
        Cable cable = new Cable(id, pointList, level, color1);
        int playerHoldingMapSize = buf.readVarInt();
        for (int i = 0; i < playerHoldingMapSize; i++) {
            cable.addPlayerHoldingPoint(buf.readVarInt(), buf.readVarInt());
        }
        return cable;
    }

    public Int2IntMap getPlayerHoldingPointMap() {
        return playerHoldingPointMap;
    }

    public void updateFromCable(Cable cable) {
        ticksSinceUpdate = 0;
        color = cable.color;
        if (points.size() > cable.points.size()) {
            for (int i = cable.points.size() - 1; i < cable.points.size(); i++) {
                points.removeLast();
            }
        }
        if (cable.points.size() > points.size()) {
            points.addAll(cable.points.subList(points.size(), cable.points.size())); //TODO: Test this
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
            this.updatePointInBlock(point);
        }
    }

    public void updatePointInBlock(Point point) {
        Vec3 vec3 = point.getPosition();
        BlockPos pos = BlockPos.containing(vec3);
        BlockHitResult blockHitResult = level.clip(new ClipContext(vec3.add(0.01f, 0.01f, 0.01f), vec3.subtract(0.01f, 0.01f, 0.01f), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        point.inBlock = blockHitResult.getType() == HitResult.Type.BLOCK;
    }

    public UUID getId() {
        return id;
    }

    public void addPlayerHoldingPoint(int playerId, int pointIndex) {
        playerHoldingPointMap.put(playerId, pointIndex);
    }

    public boolean hasPlayerHolding(int playerUUID) {
        return playerHoldingPointMap.containsKey(playerUUID);
    }

    public Pair<Point, Integer> getPlayerHeldPoint(int playerUUID) {
        if (playerHoldingPointMap.containsKey(playerUUID)) {
            int index = playerHoldingPointMap.get(playerUUID);
            if (points.size() > index) {
                return new Pair<>(points.get(index), index);
            }
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

    public void stopPlayerDrag(int playerUUID) {
        playerHoldingPointMap.remove(playerUUID);
    }

    public Color getColor() {
        return color;
    }

    public static Vec3 getAnchoredPoint(BlockPos pos, Direction face) {
        return pos.getCenter().add(pos.getCenter().subtract(pos.relative(face).getCenter()).scale(-0.45));
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
        private Direction attachedFace;
        private BlockPos attachedPoint;

        public Point(Vec3 position) {
            this.position = position;
            this.prevPosition = position;
        }

        public void setAnchor(BlockPos pos, Direction attachedFace) {
            this.attachedPoint = pos;
            this.attachedFace = attachedFace;
        }

        public void setAttachedFace(Direction attachedFace) {
            this.attachedFace = attachedFace;
        }

        public void setAttachedPoint(BlockPos attachedPoint) {
            this.attachedPoint = attachedPoint;
        }

        public BlockPos getAttachedPoint() {
            return attachedPoint;
        }

        public Direction getAttachedFace() {
            return attachedFace;
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
