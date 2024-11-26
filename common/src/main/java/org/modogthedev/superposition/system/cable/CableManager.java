package org.modogthedev.superposition.system.cable;

import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.client.renderer.CableRenderer;
import org.modogthedev.superposition.core.SuperpositionMessages;
import org.modogthedev.superposition.networking.packet.CableSyncS2CPacket;
import org.modogthedev.superposition.networking.packet.PlayerDropCableC2SPacket;
import org.modogthedev.superposition.networking.packet.PlayerGrabCableC2SPacket;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.SuperpositionConstants;
import org.modogthedev.superposition.util.Vec3LerpComponent;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.util.*;

public class CableManager {
    private static final HashMap<Level, HashMap<UUID, Cable>> cables = new HashMap<>();
    private static final HashMap<Player, Cable> playersDraggingCables = new HashMap<>();
    private static final HashMap<Level, HashMap<UUID, Cable>> clientCables = new HashMap<>();
    private static final HashMap<Player, Cable> clientPlayersDraggingCables = new HashMap<>();
    private static int grabTimer = 0;

    private static void ifAbsent(Level level) {
        if (!getCablesMap(level).containsKey(level)) {
            getCablesMap(level).put(level, new HashMap<>());
        }
    }

    public static HashMap<Level, HashMap<UUID, Cable>> getCablesMap(Level level) {
        return level.isClientSide ? clientCables : cables;
    }

    public static Collection<Cable> getCables(Level level) {
        return level.isClientSide ? clientCables.get(level).values() : cables.get(level).values();
    }

    public static HashMap<Player, Cable> getPlayersDraggingCablesMap(Level level) {
        return level.isClientSide ? playersDraggingCables : clientPlayersDraggingCables;
    }

    public static void tick(ServerLevel level) {
        ifAbsent(level);
        for (Cable cable : getCables(level)) {
            cable.updatePhysics();
        }
        dragPlayers(level);
        for (UUID uuid : getCablesMap(level).get(level).keySet()) {
            Cable cable = getCablesMap(level).get(level).get(uuid);
            CableSyncS2CPacket packet = new CableSyncS2CPacket(cable, uuid, false);
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                float maxDistance = cable.getPoints().size() + 100;
                if (cable.getPoints().get(0).getPosition().distanceTo(player.position()) < maxDistance)
                    SuperpositionMessages.sendToPlayer(packet, player);
            }
        }
    }

    public static void clientTick(Level level) {
        if (grabTimer > 0)
            grabTimer--;
        if (CableRenderer.detachDelta > 0)
            CableRenderer.detachDelta = CableRenderer.detachDelta - 0.2f;
        ifAbsent(level);
        CableRenderer.stretch = 0;
        for (Cable cable : getCables(level)) {
            cable.updatePhysics();
        }
        dragPlayers(level);
    }

    public static void dragPlayers(Level level) {
        for (Cable cable : getCables(level)) {
            for (UUID uuid : cable.getPlayerHoldingPointMap().keySet()) {
                Player player = level.getPlayerByUUID(uuid);
                float longestSegment = 0f;
                for (int i = 1; i < (cable.getPoints().size()); i++) {
                    if (longestSegment < cable.getPoints().get(i).getLength())
                        longestSegment = cable.getPoints().get(i).getLength();
                }
                Pair<Cable.Point, Integer> pointIndexPair = cable.getPlayerHeldPoint(uuid);
                Cable.Point playerPoint = pointIndexPair.getA();
                Vec3 holdGoalPos = player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(2));
            }
        }
    }

    public static void playerUsesCable(Player player, Vec3 vec3, Color color) {
        for (Cable cable : getCables(player.level())) {
            if (cable.hasPlayerHolding(player.getUUID())) {
                playerFinishDraggingCable(player, vec3);
                return;
            }
        }
        playerStartCable(vec3, player.level(), player, color);
    }

    public static UUID getCableUUID(Level level, Cable cable) {
        for (UUID uuid : getCablesMap(level).get(level).keySet()) {
            if (cable.equals(getCablesMap(level).get(level).get(uuid)))
                return uuid;
        }
        return null;
    }


    public static EventResult playerUseEvent(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        Vec3 anchorPosition = pos.getCenter().add(pos.getCenter().subtract(pos.relative(face).getCenter()).scale(-0.45));
        if (player.isShiftKeyDown()) {
            if (player.getItemInHand(hand).is(Items.AIR)) {
                for (Cable cable : getCables(player.level())) {
                    if (cable.hasPlayerHolding(player.getUUID())) {
                        playerFinishDraggingCable(player, anchorPosition);
                        return EventResult.interruptTrue();
                    }
                }
                CableClipResult cableClipResult = new CableClipResult(player.getEyePosition(), 8, player.level());
                Pair<Cable, Cable.Point> rayCast = cableClipResult.rayCastForClosest(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(5)), .7f);
                if (rayCast != null) {
                    Cable cable = rayCast.getA();
                    cable.addPlayerHoldingPoint(player.getUUID(), cable.getPointIndex(rayCast.getB()));
                    if (!player.level().isClientSide) {
                        CableSyncS2CPacket packet = new CableSyncS2CPacket(cable, getCableUUID(player.level(), cable), false);
                        for (ServerPlayer player1 : player.level().getServer().getPlayerList().getPlayers()) {
                            float maxDistance = cable.getPoints().size() + 100;
                            if (cable.getPoints().get(0).getPosition().distanceTo(player.position()) < maxDistance)
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
                if (player.getItemInHand(hand).is(Items.AIR)) {
                    for (Cable cable : getCables(player.level())) {
                        if (cable.hasPlayerHolding(player.getUUID())) {
                            cable.stopPlayerDrag(player.getUUID());
                            SuperpositionMessages.sendToServer(new PlayerDropCableC2SPacket());
                            return;
                        }
                    }
                }
                CableClipResult cableClipResult = new CableClipResult(player.getEyePosition(), 8, player.level());
                Pair<Cable, Cable.Point> rayCast = cableClipResult.rayCastForClosest(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(5)), .7f);
                if (rayCast != null) {
                    SuperpositionMessages.sendToServer(new PlayerGrabCableC2SPacket());
                    Cable cable = rayCast.getA();
                    cable.addPlayerHoldingPoint(player.getUUID(), cable.getPointIndex(rayCast.getB()));
                    if (!player.level().isClientSide) {
                        CableSyncS2CPacket packet = new CableSyncS2CPacket(cable, getCableUUID(player.level(), cable), false);
                        for (ServerPlayer player1 : player.level().getServer().getPlayerList().getPlayers()) {
                            float maxDistance = cable.getPoints().size() + 100;
                            if (cable.getPoints().get(0).getPosition().distanceTo(player.position()) < maxDistance)
                                SuperpositionMessages.sendToPlayer(packet, player1);
                        }
                    }
                }
            }
        }
    }

    private static void playerStartCable(Vec3 pos, Level level, Player player, Color color) {
        if (player.level().isClientSide)
            return;
        Cable newCable = new Cable(pos, player.getRopeHoldPosition(0), SuperpositionConstants.cableSpawnAmount, level, color);
        newCable.setPlayerHolding(player);
        addCable(newCable, level, UUID.randomUUID());
    }

    public static void playerFinishDraggingCable(Player player, Vec3 vec3) {
        for (Cable cable : getCables(player.level())) {
            if (cable.hasPlayerHolding(player.getUUID())) {
                Pair<Cable.Point, Integer> pointIndexPair = cable.getPlayerHeldPoint(player.getUUID());
                Cable.Point anchorPoint = new Cable.Point(pointIndexPair.getA().getPosition());
                anchorPoint.lerpedPos = new Vec3LerpComponent(vec3, anchorPoint.getPosition(), 5);
                cable.replacePointAtIndex(pointIndexPair.getB(), anchorPoint);
                cable.stopPlayerDrag(player.getUUID());
                if (!player.level().isClientSide) {
                    CableSyncS2CPacket packet = new CableSyncS2CPacket(cable, getCableUUID(player.level(), cable), false);
                    for (ServerPlayer player1 : player.level().getServer().getPlayerList().getPlayers()) {
                        float maxDistance = cable.getPoints().size() + 100;
                        if (cable.getPoints().get(0).getPosition().distanceTo(player.position()) < maxDistance)
                            SuperpositionMessages.sendToPlayer(packet, player1);
                    }
                }
            }
        }
    }

    public static void playerExtendsCable(Player player, int amount) {
        for (Cable cable : getCables(player.level())) {
            if (cable.hasPlayerHolding(player.getUUID())) {
                int index = cable.getPlayerHeldPoint(player.getUUID()).getB();
                cable.stopPlayerDrag(player.getUUID());
                for (int i = 0; i < amount; i++)
                    cable.addPointAtIndex(index, new Cable.Point(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(3))));
                cable.addPlayerHoldingPoint(player.getUUID(), index + amount);
            }
        }
    }

    public static void playerShrinksCable(Player player) {
        for (Cable cable : getCables(player.level())) {
            if (cable.hasPlayerHolding(player.getUUID())) {
                int index = cable.getPlayerHeldPoint(player.getUUID()).getB();
                cable.stopPlayerDrag(player.getUUID());
                cable.getPoints().remove(index);
                cable.addPlayerHoldingPoint(player.getUUID(), index - 1);
            }
        }
    }

    public static void addCable(Cable cable, Level level, UUID uuid) {
        ifAbsent(level);
        getCablesMap(level).get(level).put(uuid, cable);
    }

    public static Collection<Cable> getLevelCables(Level level) {
        ifAbsent(level);
        return getCables(level);
    }

    public static Collection<Cable> getPlayerDraggingCables(Level level) {
        return (getPlayersDraggingCablesMap(level).values());
    }
}
