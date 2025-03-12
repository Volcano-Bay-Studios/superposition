package org.modogthedev.superposition.system.cable;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.PointLight;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.core.SuperpositionTags;
import org.modogthedev.superposition.system.cable.rope_system.AnchorConstraint;
import org.modogthedev.superposition.system.cable.rope_system.RopeNode;
import org.modogthedev.superposition.system.cable.rope_system.RopeSimulation;
import org.modogthedev.superposition.system.signal.Signal;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class Cable {

    private final UUID id;
    private Int2IntMap playerHoldingPointMap = new Int2IntArrayMap();
    private Level level;
    public float radius = SuperpositionConstants.cableRadius;
    private final RopeSimulation ropeSimulation;
    private Player playerHolding;
    private Color color;
    private final boolean emitsLight;
    private List<PointLight> pointLights;
    private float brightness;

    private LightRenderer lightRenderer = null;

    public Cable(UUID id, Vec3 starAnchor, Vec3 endAnchor, int points, Level level, Color color, boolean emitsLight) {
        this.id = id;
        this.ropeSimulation = new RopeSimulation(radius);
        ropeSimulation.createRope(points, starAnchor, endAnchor);
        this.level = level;
        this.color = color;
        this.emitsLight = emitsLight;
    }

    private Cable(UUID id, RopeSimulation ropeSimulation, Level level, Color color, boolean emitsLight) {
        this.id = id;
        this.ropeSimulation = ropeSimulation;
        this.level = level;
        this.color = color;
        this.emitsLight = emitsLight;
    }

    public void updatePhysics() {
        if (!playerHoldingPointMap.isEmpty()) {
            ropeSimulation.invalidateSleepTime();
        }
        freeStuckPoints();
        if (!isSleeping()) {
            ropeSimulation.simulate(level);
        }
        this.sendSignal();
    }

    private void sendSignal() {
        if (level != null && playerHolding == null) {
            RopeNode firstNode = ropeSimulation.getNodes().getFirst();
            RopeNode lastNode = ropeSimulation.getNodes().getLast();


            if (firstNode.getAnchor() == null) {
                return;
            }

            BlockPos startPos = firstNode.getAnchor().getAnchorBlock();
            if (emitsLight && level.isLoaded(startPos)) {
                BlockEntity start = level.getBlockEntity(startPos);
                if (start instanceof SignalActorBlockEntity startSignalActor) {
                    updateColor(startSignalActor.getSideSignals(firstNode.getAnchor().getDirection()));
                }
            }

            if (lastNode.getAnchor() == null) {
                if (emitsLight) {
                    List<Signal> signalList = CablePassthroughManager.getSignalsFromBlock(level, startPos);
                    if (signalList != null && !signalList.isEmpty()) {
                        updateColor(signalList);
                    }
                }
                return;
            }

            BlockPos endPos = lastNode.getAnchor().getAnchorBlock();

            if (level.isLoaded(startPos) && level.isLoaded(endPos)) {

                BlockEntity start = level.getBlockEntity(startPos);
                BlockEntity end = level.getBlockEntity(endPos);

                if (start instanceof SignalActorBlockEntity startSignalActor && end instanceof SignalActorBlockEntity endSignalActor) {

                    List<Signal> signalList = startSignalActor.getSideSignals(firstNode.getAnchor().getDirection());
                    if (signalList != null && !signalList.isEmpty() && startSignalActor != endSignalActor) {
                        endSignalActor.addSignals(new Object(), signalList, lastNode.getAnchor().getDirection());
                    }

                } else if (start instanceof SignalActorBlockEntity startSignalActor) {
                    if (level.getBlockState(endPos).is(SuperpositionTags.SIGNAL_OFFSET)) {
                        CablePassthroughManager.addSignalsToBlock(
                                level, endPos.relative(lastNode.getAnchor().getDirection()),
                                startSignalActor.getSideSignals(firstNode.getAnchor().getDirection()),
                                lastNode.getAnchor().getDirection()

                        );
                    } else {

                        CablePassthroughManager.addSignalsToBlock(
                                level, endPos,
                                startSignalActor.getSideSignals(firstNode.getAnchor().getDirection()),
                                lastNode.getAnchor().getDirection()
                        );
                    }

                } else {

                    List<Signal> signalList = CablePassthroughManager.getSignalsFromBlock(level, startPos);
                    if (signalList != null) {
                        if (end instanceof SignalActorBlockEntity endSignalActor) {
                            endSignalActor.putSignalsFace(new Object(), signalList, lastNode.getAnchor().getDirection());
                        } else {
                            CablePassthroughManager.addSignalsToBlock(level, endPos, signalList, lastNode.getAnchor().getDirection());
                        }
                        if (emitsLight) {
                            updateColor(signalList);
                        }
                    }

                }

            }
        }
    }

    private void updateColor(List<Signal> signalList) {
        if (signalList == null || signalList.isEmpty()) {
            return;
        }
        Signal signal = signalList.getFirst();
        brightness = signal.getAmplitude();
        if (signal.getEncodedData() != null) {
            Color color;
            int colorInt = signal.getEncodedData().intValue();
            color = new Color(colorInt);
            if (color != null) {
                this.color = color;
            }
        }
    }

    public void updateLights(float partialTicks) {
        if (lightRenderer == null) {
            lightRenderer = VeilRenderSystem.renderer().getLightRenderer();
        }
        if (emitsLight) {
            if (pointLights == null) {
                pointLights = new ArrayList<>();
            }
            if (pointLights.size() > ropeSimulation.getNodesCount()) {
                ListIterator<PointLight> iterator = pointLights.listIterator();
                while (iterator.hasNext()) {
                    int i = iterator.nextIndex();
                    PointLight point = iterator.next();
                    if (i >= ropeSimulation.getNodesCount()) {
                        lightRenderer.removeLight(point);
                        iterator.remove();
                    }
                }
            } else if (pointLights.size() < ropeSimulation.getNodesCount()) {
                for (int i = pointLights.size(); i < ropeSimulation.getNodes().size(); i++) {
                    pointLights.add(new PointLight());
                    lightRenderer.addLight(pointLights.get(i));
                }
            }

            for (int i = 0; i < ropeSimulation.getNodesCount(); i++) {
                updateLight(pointLights.get(i), ropeSimulation.getNode(i));
            }
        }
    }

    private void freeStuckPoints() {
        for (int i = 0; i < ropeSimulation.getNodesCount() - 1; i++) {
            RopeNode point = ropeSimulation.getNode(i);
            RopeNode lastPoint = ropeSimulation.getNode(i + 1);
            float distance = (float) point.getPosition().distanceTo(lastPoint.getPosition());
            if (distance > SuperpositionConstants.cableRadius * 3) {
                point.addNextPosition(lastPoint.getPosition());
                lastPoint.addNextPosition(point.getPosition());
            }
        }
    }

    private void updateLight(PointLight light, RopeNode point) {
        light.setPosition(point.getPosition().x, point.getPosition().y, point.getPosition().z);
        light.setBrightness((float) Mth.map(brightness, 1, 200, 0.15, 0.2));
        light.setRadius(Mth.map(brightness, 1, 200, 3, 8));
        light.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    public void shrink() {
        if (ropeSimulation.getNodesCount() > 4)
            ropeSimulation.resizeRope(ropeSimulation.getNodesCount() - 1);
    }

    public void setPlayerHolding(Player player) {
        this.addPlayerHoldingPoint(player.getId(), getPoints().size() - 1);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<RopeNode> getPoints() {
        return ropeSimulation.getNodes();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(color.getRGB());
        buf.writeBoolean(emitsLight);
        buf.writeVarInt(ropeSimulation.getNodesCount());
        for (RopeNode point : ropeSimulation.getNodes()) {
            buf.writeVec3(point.getPosition());
            buf.writeVec3(point.getPrevPosition());
            AnchorConstraint constraint = point.getAnchor();
            buf.writeBoolean(constraint != null);
            if (constraint != null) {
                buf.writeEnum(constraint.getDirection());
                buf.writeBlockPos(constraint.getAnchorBlock());
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
        boolean emitsLight = buf.readBoolean();
        int size = buf.readVarInt();
        RopeSimulation ropeSimulation = new RopeSimulation(SuperpositionConstants.cableRadius);
        for (int i = 0; i < size; i++) {
            RopeNode newPoint = new RopeNode(buf.readVec3());
            ropeSimulation.addNode(newPoint);
            newPoint.setPrevPosition(buf.readVec3());
            if (buf.readBoolean()) {
                newPoint.setAnchor(buf.readEnum(Direction.class), buf.readBlockPos());
            }
        }
        Cable cable = new Cable(id, ropeSimulation, level, color1, emitsLight);
        int playerHoldingMapSize = buf.readVarInt();
        for (int i = 0; i < playerHoldingMapSize; i++) {
            cable.addPlayerHoldingPoint(buf.readVarInt(), buf.readVarInt());
        }
        return cable;
    }

    public Int2IntMap getPlayerHoldingPointMap() {
        return playerHoldingPointMap;
    }

    public void updateFromCable(Cable cable, boolean isHard) {
        color = cable.color;
        ropeSimulation.removeAllConstraints();
        ropeSimulation.resizeRope(cable.getPointsCount());
        List<RopeNode> targetPoints = cable.getPoints();
        for (int i = 0; i < ropeSimulation.getNodesCount(); i++) {
            if (this.playerHoldingPointMap.size() != cable.playerHoldingPointMap.size()) {
                ropeSimulation.getNode(i).setPosition(targetPoints.get(i).getPosition());
//                ropeSimulation.getNode(i).setPrevPosition(targetPoints.get(i).getPrevPosition());
            } else {
//                ropeSimulation.getNode(i).setPrevPosition(ropeSimulation.getNode(i).getPosition());
                ropeSimulation.getNode(i).setPrevPosition(targetPoints.get(i).getPrevPosition().lerp(ropeSimulation.getNode(i).getPrevPosition(),0.8f));
                ropeSimulation.getNode(i).setPosition(targetPoints.get(i).getPosition().lerp(ropeSimulation.getNode(i).getPosition(),0.8f));
            }
            AnchorConstraint newAnchor = targetPoints.get(i).getAnchor();
            if (newAnchor != null) {
                ropeSimulation.getNode(i).setAnchor(newAnchor.getDirection(), newAnchor.getAnchorBlock());
            } else {
                targetPoints.get(i).removeAnchor();
            }
        }
        ropeSimulation.recalculateBaseRopeConstraints();
        this.playerHoldingPointMap = new Int2IntArrayMap(cable.playerHoldingPointMap);
    }

    public float calculateLength() {
        float length = 0;
        for (RopeNode node : ropeSimulation.getNodes()) {
            RopeNode next = node.getNext();
            if (next != null) length += (float) node.getPosition().distanceTo(next.getPosition());
        }
        return length;
    }

    private int getPointsCount() {
        return ropeSimulation.getNodesCount();
    }

    public UUID getId() {
        return id;
    }

    public void addPlayerHoldingPoint(int playerId, int pointIndex) {
        getPoints().get(pointIndex).removeAnchor();
        playerHoldingPointMap.put(playerId, pointIndex);
    }

    public boolean hasPlayerHolding(int playerUUID) {
        return playerHoldingPointMap.containsKey(playerUUID);
    }

    public Pair<RopeNode, Integer> getPlayerHeldPoint(int playerUUID) {
        if (playerHoldingPointMap.containsKey(playerUUID)) {
            int index = playerHoldingPointMap.get(playerUUID);
            if (ropeSimulation.getNodesCount() > index) {
                return new Pair<>(ropeSimulation.getNode(index), index);
            }
        }
        return null;
    }

    public void addPointAtIndex(int index, RopeNode point) {
        ropeSimulation.addNode(index, point);
        ropeSimulation.recalculateBaseRopeConstraints();
    }

    public int getPointIndex(RopeNode point) {
        return ropeSimulation.getNodes().indexOf(point);
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

    public boolean isSleeping() {
        return ropeSimulation.isSleeping();
    }

    public void preSimulate() {
        for (RopeNode node : getPoints()) {
            node.preSimulate();
        }
    }
}
