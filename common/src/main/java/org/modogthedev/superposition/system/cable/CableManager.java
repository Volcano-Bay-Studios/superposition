package org.modogthedev.superposition.system.cable;

import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.client.renderer.CableRenderer;
import org.modogthedev.superposition.core.SuperpositionMessages;
import org.modogthedev.superposition.networking.packet.CableSyncS2CPacket;
import org.modogthedev.superposition.networking.packet.PlayerDropCableC2SPacket;
import org.modogthedev.superposition.networking.packet.PlayerGrabCableC2SPacket;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.util.Vec3LerpComponent;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.util.*;

public class CableManager {
    private static final Map<Level, Map<UUID, Cable>> cables = new HashMap<>();
    private static final Map<Player, Cable> playersDraggingCables = new HashMap<>();
    private static final Map<Level, Map<UUID, Cable>> clientCables = new HashMap<>();
    private static final Map<Player, Cable> clientPlayersDraggingCables = new HashMap<>();
    private static int grabTimer = 0;

    public static Map<Level, Map<UUID, Cable>> getCablesMap(Level level) {
        return level.isClientSide() ? clientCables : cables;
    }

    public static @Nullable Map<UUID, Cable> getCables(Level level) {
        return level.isClientSide() ? clientCables.get(level) : cables.get(level);
    }

    public static Map<Player, Cable> getPlayersDraggingCablesMap(Level level) {
        return level.isClientSide() ? playersDraggingCables : clientPlayersDraggingCables;
    }

    public static void tick(ServerLevel level) {
        Map<UUID, Cable> cables = getCables(level);
        if (cables != null) {
            for (Cable cable : cables.values()) {
                cable.updatePhysics();
            }
            dragPlayers(level);

            for (Map.Entry<UUID, Cable> entry : cables.entrySet()) {
                Cable cable = entry.getValue();
                CableSyncS2CPacket packet = new CableSyncS2CPacket(cable, entry.getKey(), false);
                for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                    float maxDistance = cable.getPoints().size() + 100;
                    if (cable.getPoints().getFirst().getPosition().distanceTo(player.position()) < maxDistance) {
                        SuperpositionMessages.sendToPlayer(packet, player);
                    }
                }
            }
        }
    }

    public static void clientTick(Level level) {
        if (grabTimer > 0)
            grabTimer--;
        if (CableRenderer.detachDelta > 0)
            CableRenderer.detachDelta = CableRenderer.detachDelta - 0.2f;
        CableRenderer.stretch = 0;
        Map<UUID, Cable> cables = getCables(level);
        if (cables != null) {
            for (Cable cable : cables.values()) {
                cable.updatePhysics();
            }
            dragPlayers(level);
        }
    }

    public static void dragPlayers(Level level) {
            for (Cable cable : getLevelCables(level)) {
                for (int id : cable.getPlayerHoldingPointMap().keySet()) {
                    if (level.getEntity(id) instanceof Player player) {
                        float longestSegment = 0f;
                        for (int i = 1; i < (cable.getPoints().size()); i++) {
                            if (longestSegment < cable.getPoints().get(i).getLength())
                                longestSegment = cable.getPoints().get(i).getLength();
                        }
                        Pair<Cable.Point, Integer> pointIndexPair = cable.getPlayerHeldPoint(id);
                        Cable.Point playerPoint = pointIndexPair.getA();
                        Vec3 holdGoalPos = player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(2));
                    }
                }
        }
    }

    public static void playerUsesCable(Player player, BlockPos pos, Color color, Direction face) {
            for (Cable cable : getLevelCables(player.level())) {
                if (cable.hasPlayerHolding(player.getId())) {
                    playerFinishDraggingCable(player, pos, face);
                    return;
                }
            }
            playerStartCable(pos, face, player.level(), player, color);
    }

    public static UUID getCableUUID(Level level, Cable cable) {
        Map<UUID, Cable> cables = getCablesMap(level).get(level);
        if(cables==null){
            return null;
        }

        for (Map.Entry<UUID, Cable> entry : cables.entrySet()) {
            if (cable.equals(entry.getValue()))
                return entry.getKey();
        }
        return null;
    }


    public static EventResult playerUseEvent(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        if (player.isShiftKeyDown()) {
            if (player.getItemInHand(hand).is(Items.AIR)) {
                int id = player.getId();
                    for (Cable cable : getLevelCables(player.level())) {
                        if (cable.hasPlayerHolding(id)) {
                            playerFinishDraggingCable(player, pos, face);
                            return EventResult.interruptTrue();
                        }
                    }
                CableClipResult cableClipResult = new CableClipResult(player.getEyePosition(), 8, player.level());
                Pair<Cable, Cable.Point> rayCast = cableClipResult.rayCastForClosest(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(5)), .7f);
                if (rayCast != null) {
                    Cable cable = rayCast.getA();
                    cable.addPlayerHoldingPoint(id, cable.getPointIndex(rayCast.getB()));
                    rayCast.getB().setAnchor(null,null);
                    if (!player.level().isClientSide) {
                        CableSyncS2CPacket packet = new CableSyncS2CPacket(cable, getCableUUID(player.level(), cable), false);
                        for (ServerPlayer player1 : player.level().getServer().getPlayerList().getPlayers()) {
                            float maxDistance = cable.getPoints().size() + 100;
                            if (cable.getPoints().getFirst().getPosition().distanceToSqr(player.position()) < maxDistance*maxDistance)
                                SuperpositionMessages.sendToPlayer(packet, player1);
                        }
                    }
                    return EventResult.interruptTrue();
                }
            }
        }
        return EventResult.interruptDefault();
    }

    public static void playerDropCableEvent(Player player, InteractionHand hand) {
        if (grabTimer == 0) {
            grabTimer = 4;
            if (player.isShiftKeyDown()) {
                int id = player.getId();
                if (player.getItemInHand(hand).is(Items.AIR)) {
                    Map<UUID, Cable> cables = getCables(player.level());
                    if (cables != null) {
                        for (Cable cable : cables.values()) {
                            if (cable.hasPlayerHolding(id)) {
                                cable.stopPlayerDrag(id);
                                SuperpositionMessages.sendToServer(new PlayerDropCableC2SPacket());
                                return;
                            }
                        }
                    }
                }
                CableClipResult cableClipResult = new CableClipResult(player.getEyePosition(), 8, player.level());
                Pair<Cable, Cable.Point> rayCast = cableClipResult.rayCastForClosest(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(5)), .7f);
                if (rayCast != null) {
                    SuperpositionMessages.sendToServer(new PlayerGrabCableC2SPacket());
                    Cable cable = rayCast.getA();
                    cable.addPlayerHoldingPoint(id, cable.getPointIndex(rayCast.getB()));
                    if (!player.level().isClientSide) {
                        CableSyncS2CPacket packet = new CableSyncS2CPacket(cable, getCableUUID(player.level(), cable), false);
                        for (ServerPlayer player1 : player.level().getServer().getPlayerList().getPlayers()) {
                            float maxDistance = cable.getPoints().size() + 100;
                            if (cable.getPoints().getFirst().getPosition().distanceTo(player.position()) < maxDistance)
                                SuperpositionMessages.sendToPlayer(packet, player1);
                        }
                    }
                }
            }
        }
    }

    private static void playerStartCable(BlockPos pos, Direction face, Level level, Player player, Color color) {
        if (player.level().isClientSide)
            return;
        Vec3 anchorPosition = Cable.getAnchoredPoint(pos, face);
        Cable newCable = new Cable(anchorPosition, player.getRopeHoldPosition(0), SuperpositionConstants.cableSpawnAmount, level, color);
        newCable.getPoints().getFirst().setAnchor(pos, face);
        newCable.setPlayerHolding(player);
        addCable(newCable, level, UUID.randomUUID());
    }

    public static void playerFinishDraggingCable(Player player, BlockPos pos, Direction face) {
            for (Cable cable : getLevelCables(player.level())) {
                int id = player.getId();
                if (cable.hasPlayerHolding(id)) {
                    Pair<Cable.Point, Integer> pointIndexPair = cable.getPlayerHeldPoint(id);
                    Cable.Point anchorPoint = new Cable.Point(pointIndexPair.getA().getPosition());
                    Vec3 anchorPosition = pos.getCenter();
                    if (face != null) {
                        anchorPosition = Cable.getAnchoredPoint(pos, face);
                        anchorPoint.setAnchor(pos, face);
                    }
                    anchorPoint.lerpedPos = new Vec3LerpComponent(anchorPosition, anchorPoint.getPosition(), 5);
                    cable.replacePointAtIndex(pointIndexPair.getB(), anchorPoint);
                    cable.stopPlayerDrag(id);
                    if (!player.level().isClientSide) {
                        CableSyncS2CPacket packet = new CableSyncS2CPacket(cable, getCableUUID(player.level(), cable), false);
                        for (ServerPlayer player1 : player.level().getServer().getPlayerList().getPlayers()) {
                            float maxDistance = cable.getPoints().size() + 100;
                            if (cable.getPoints().getFirst().getPosition().distanceTo(player.position()) < maxDistance)
                                SuperpositionMessages.sendToPlayer(packet, player1);
                        }
                    }
                }
        }
    }

    public static void playerExtendsCable(Player player, int amount) {
            for (Cable cable : getLevelCables(player.level())) {
                int id = player.getId();
                if (cable.hasPlayerHolding(id)) {
                    int index = cable.getPlayerHeldPoint(id).getB();
                    cable.stopPlayerDrag(id);
                    for (int i = 0; i < amount; i++)
                        cable.addPointAtIndex(index, new Cable.Point(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(3))));
                    cable.addPlayerHoldingPoint(id, index + amount);
                }
            }
    }

    public static void playerShrinksCable(Player player) {
        Map<UUID, Cable> cables = getCables(player.level());
        if (cables == null) {
            return;
        }

        for (Map.Entry<UUID, Cable> entry : cables.entrySet()) {
            Cable cable = entry.getValue();
            if (cable.getPoints().size() < 4) {
                removeCable(entry.getKey());
                return;
            }

            int id = player.getId();
            if (cable.hasPlayerHolding(id)) {
                int index = cable.getPlayerHeldPoint(id).getB();
                cable.stopPlayerDrag(id);
                cable.getPoints().remove(index);
                cable.addPlayerHoldingPoint(id, index - 1);
            }
        }
    }

    public static void addCable(Cable cable, Level level, UUID uuid) {
        getCablesMap(level).computeIfAbsent(level, unused -> new HashMap<>()).put(uuid, cable);
    }

    public static Collection<Cable> getLevelCables(Level level) {
        Map<UUID, Cable> cables = getCables(level);
        return cables != null ? cables.values() : Collections.emptyList();
    }

    public static void removeCable(UUID cableId) {

    }
}
