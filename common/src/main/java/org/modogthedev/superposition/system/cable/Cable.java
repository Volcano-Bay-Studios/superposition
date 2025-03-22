package org.modogthedev.superposition.system.cable;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.core.SuperpositionTags;
import org.modogthedev.superposition.system.cable.rope_system.AnchorConstraint;
import org.modogthedev.superposition.system.cable.rope_system.RopeNode;
import org.modogthedev.superposition.system.cable.rope_system.RopeSimulation;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.util.List;
import java.util.UUID;

public class Cable {

    private final UUID id;
    private Int2IntMap playerHoldingPointMap = new Int2IntArrayMap();
    private Level level;
    public float radius = SuperpositionConstants.cableRadius;
    private final RopeSimulation ropeSimulation;
    private Color color;
    private final boolean emitsLight;
    private float brightness;
    private int stretchGrace = 0;

    private CableClientState clientState;

    public Cable(UUID id, Vec3 starAnchor, Vec3 endAnchor, int points, Level level, Color color, boolean emitsLight) {
        this.id = id;
        this.ropeSimulation = new RopeSimulation(this.radius, false);
        this.ropeSimulation.createRope(points, starAnchor, endAnchor);
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
        if (this.stretchGrace > 0) {
            this.stretchGrace--;
        }
        if (!this.playerHoldingPointMap.isEmpty()) {
            this.ropeSimulation.invalidateSleepTime();
        }
        this.freeStuckPoints();
        if (!this.isSleeping()) {
            this.ropeSimulation.simulate(this.level);
        }
        this.sendSignal();
    }

    private void sendSignal() {
        if (this.level != null) {
            RopeNode firstNode = this.ropeSimulation.getNodes().getFirst();
            RopeNode lastNode = this.ropeSimulation.getNodes().getLast();


            if (firstNode.getAnchor() == null) {
                return;
            }

            BlockPos startPos = firstNode.getAnchor().getAnchorBlock();
            if (this.emitsLight && this.level.isLoaded(startPos)) {
                BlockEntity start = this.level.getBlockEntity(startPos);
                if (start instanceof SignalActorBlockEntity startSignalActor) {
                    this.updateColor(startSignalActor.getSideSignals(firstNode.getAnchor().getDirection()));
                }
            }

            if (lastNode.getAnchor() == null) {
                if (this.emitsLight) {
                    List<Signal> signalList = CablePassthroughManager.getSignalsFromBlock(this.level, startPos);
                    if (signalList != null && !signalList.isEmpty()) {
                        this.updateColor(signalList);
                    }
                }
                return;
            }

            BlockPos endPos = lastNode.getAnchor().getAnchorBlock();

            if (this.level.isLoaded(startPos) && this.level.isLoaded(endPos)) {

                BlockEntity start = this.level.getBlockEntity(startPos);
                BlockEntity end = this.level.getBlockEntity(endPos);

                if (start instanceof SignalActorBlockEntity startSignalActor && end instanceof SignalActorBlockEntity endSignalActor) {

                    List<Signal> signalList = startSignalActor.getSideSignals(firstNode.getAnchor().getDirection());
                    if (signalList != null && !signalList.isEmpty() && startSignalActor != endSignalActor) {
                        endSignalActor.addSignals(new Object(), signalList, lastNode.getAnchor().getDirection());
                    }

                } else if (start instanceof SignalActorBlockEntity startSignalActor) {
                    if (this.level.getBlockState(endPos).is(SuperpositionTags.SIGNAL_OFFSET)) {
                        CablePassthroughManager.addSignalsToBlock(
                                this.level, endPos.relative(lastNode.getAnchor().getDirection()),
                                startSignalActor.getSideSignals(firstNode.getAnchor().getDirection()),
                                lastNode.getAnchor().getDirection()

                        );
                    } else {

                        CablePassthroughManager.addSignalsToBlock(
                                this.level, endPos,
                                startSignalActor.getSideSignals(firstNode.getAnchor().getDirection()),
                                lastNode.getAnchor().getDirection()
                        );
                    }

                } else {

                    List<Signal> signalList = CablePassthroughManager.getSignalsFromBlock(this.level, startPos);
                    if (signalList != null) {
                        if (end instanceof SignalActorBlockEntity endSignalActor) {
                            endSignalActor.putSignalsFace(new Object(), signalList, lastNode.getAnchor().getDirection());
                        } else {
                            CablePassthroughManager.addSignalsToBlock(this.level, endPos, signalList, lastNode.getAnchor().getDirection());
                        }
                        if (this.emitsLight) {
                            this.updateColor(signalList);
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
        this.brightness = signal.getAmplitude();
        EncodedData<?> encodedData = signal.getEncodedData();
        if (encodedData != null) {
            this.color = new Color(encodedData.intValue());
        }
    }

    private void freeStuckPoints() {
        for (int i = 0; i < this.ropeSimulation.getNodeCount() - 1; i++) {
            RopeNode point = this.ropeSimulation.getNode(i);
            RopeNode lastPoint = this.ropeSimulation.getNode(i + 1);
            float distance = (float) point.getPosition().distanceTo(lastPoint.getPosition());
            if (distance > SuperpositionConstants.cableRadius * 3) {
                point.addNextPosition(lastPoint.getPosition());
                lastPoint.addNextPosition(point.getPosition());
            }
        }
    }

    public void shrink() {
        if (this.ropeSimulation.getNodeCount() > 4)
            this.ropeSimulation.resizeRope(this.ropeSimulation.getNodeCount() - 1);
    }

    public void setPlayerHolding(Player player) {
        this.addPlayerHoldingPoint(player.getId(), this.getPoints().size() - 1);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<RopeNode> getPoints() {
        return this.ropeSimulation.getNodes();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.color.getRGB());
        buf.writeByte((this.emitsLight ? 1 : 0) | (this.ropeSimulation.isSleeping() ? 2 : 0));
        buf.writeVarInt(this.ropeSimulation.getNodeCount());
        for (RopeNode point : this.ropeSimulation.getNodes()) {
            buf.writeVec3(point.getPosition());
            buf.writeVec3(point.getPrevPosition());
            AnchorConstraint constraint = point.getAnchor();
            buf.writeBoolean(constraint != null);
            if (constraint != null) {
                buf.writeEnum(constraint.getDirection());
                buf.writeBlockPos(constraint.getAnchorBlock());
            }
        }
        buf.writeVarInt(this.playerHoldingPointMap.size());
        for (Int2IntMap.Entry entry : this.playerHoldingPointMap.int2IntEntrySet()) {
            buf.writeVarInt(entry.getIntKey());
            buf.writeVarInt(entry.getIntValue());
        }
    }

    public static Cable fromBytes(UUID id, FriendlyByteBuf buf, Level level, boolean isCreating) {
        Color color1 = new Color(buf.readInt());
        byte flags = buf.readByte();
        boolean emitsLight = (flags & 1) != 0;
        boolean sleeping = (flags & 2) != 0;
        int size = buf.readVarInt();
        RopeSimulation ropeSimulation = new RopeSimulation(SuperpositionConstants.cableRadius, sleeping);
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
        if (isCreating) {
            ropeSimulation.recalculateBaseRopeConstraints();
        }
        return cable;
    }

    public Int2IntMap getPlayerHoldingPointMap() {
        return this.playerHoldingPointMap;
    }

    public void updateFromCable(Cable cable, boolean isHard) {
        this.color = cable.color;
        this.ropeSimulation.removeAllConstraints();
        this.ropeSimulation.resizeRope(cable.getPointsCount());
        List<RopeNode> targetPoints = cable.getPoints();
        for (int i = 0; i < this.ropeSimulation.getNodeCount(); i++) {
            if (this.playerHoldingPointMap.size() != cable.playerHoldingPointMap.size()) {
                this.ropeSimulation.getNode(i).setPosition(targetPoints.get(i).getPosition());
//                ropeSimulation.getNode(i).setPrevPosition(targetPoints.get(i).getPrevPosition());
            } else {
//                ropeSimulation.getNode(i).setPrevPosition(ropeSimulation.getNode(i).getPosition());
                this.ropeSimulation.getNode(i).setPrevPosition(targetPoints.get(i).getPrevPosition().lerp(this.ropeSimulation.getNode(i).getPrevPosition(), 0.8f));
                this.ropeSimulation.getNode(i).setPosition(targetPoints.get(i).getPosition().lerp(this.ropeSimulation.getNode(i).getPosition(), 0.8f));
            }
            AnchorConstraint newAnchor = targetPoints.get(i).getAnchor();
            if (newAnchor != null) {
                this.ropeSimulation.getNode(i).setAnchor(newAnchor.getDirection(), newAnchor.getAnchorBlock());
            } else {
                targetPoints.get(i).removeAnchor();
            }
        }
        this.level = cable.level;
        this.ropeSimulation.recalculateBaseRopeConstraints();
        this.playerHoldingPointMap = new Int2IntArrayMap(cable.playerHoldingPointMap);
    }

    public float calculateLength() {
        float length = 0;
        for (RopeNode node : this.ropeSimulation.getNodes()) {
            RopeNode next = node.getNext();
            if (next != null) length += (float) node.getPosition().distanceTo(next.getPosition());
        }
        return length;
    }

    private int getPointsCount() {
        return this.ropeSimulation.getNodeCount();
    }

    public UUID getId() {
        return this.id;
    }

    public void addPlayerHoldingPoint(int playerId, int pointIndex) {
        this.stretchGrace = 4;
        if (this.getPointsCount() > pointIndex && pointIndex > -1) {
            this.getPoints().get(pointIndex).removeAnchor();
            this.playerHoldingPointMap.put(playerId, pointIndex);
        }
    }

    public boolean hasPlayerHolding(int playerUUID) {
        return this.playerHoldingPointMap.containsKey(playerUUID);
    }

    public Pair<RopeNode, Integer> getPlayerHeldPoint(int playerUUID) {
        if (this.playerHoldingPointMap.containsKey(playerUUID)) {
            int index = this.playerHoldingPointMap.get(playerUUID);
            if (this.ropeSimulation.getNodeCount() > index) {
                return new Pair<>(this.ropeSimulation.getNode(index), index);
            }
        }
        return null;
    }

    public void addPointAtIndex(int index, RopeNode point) {
        this.ropeSimulation.addNode(index, point);
        this.ropeSimulation.recalculateBaseRopeConstraints();
    }

    public int getPointIndex(RopeNode point) {
        return this.ropeSimulation.getNodes().indexOf(point);
    }

    public void stopPlayerDrag(int playerUUID) {
        this.playerHoldingPointMap.remove(playerUUID);
    }

    public Color getColor() {
        return this.color;
    }

    public @Nullable CableClientState getClientState() {
        RenderSystem.assertOnRenderThread();
        return this.clientState;
    }

    public CableClientState getRenderState(float partialTicks) {
        RenderSystem.assertOnRenderThread();
        if (this.clientState == null) {
            this.clientState = new CableClientState(this, this.ropeSimulation);
            this.clientState.update(partialTicks);
        } else if (!this.isSleeping()) {
            this.clientState.update(partialTicks);
        }
        return this.clientState;
    }

    public float getBrightness() {
        return this.brightness;
    }

    public boolean isEmitsLight() {
        return this.emitsLight;
    }

    public int getStretchGrace() {
        return this.stretchGrace;
    }

    public void setStretchGrace(int stretchGrace) {
        this.stretchGrace = stretchGrace;
    }

    public static Vec3 getAnchoredPoint(BlockPos pos, Direction face) {
        return pos.getCenter().add(pos.getCenter().subtract(pos.relative(face).getCenter()).scale(-0.45));
    }

    public boolean isSleeping() {
        return this.ropeSimulation.isSleeping();
    }

    public void preSimulate() {
        for (RopeNode node : this.getPoints()) {
            node.preSimulate();
        }
    }
}
