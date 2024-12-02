package org.modogthedev.superposition.system.cable;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.client.renderer.CableRenderer;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.networking.packet.CableSyncS2CPacket;
import org.modogthedev.superposition.networking.packet.PlayerDropCableC2SPacket;
import org.modogthedev.superposition.networking.packet.PlayerGrabCableC2SPacket;
import org.modogthedev.superposition.util.Vec3LerpComponent;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.util.*;

public class CableManager {
    private static final Map<ResourceKey<Level>, Map<UUID, Cable>> cables = new HashMap<>();
    private static final Map<Player, Cable> playersDraggingCables = new HashMap<>();
    private static final Map<ResourceKey<Level>, Map<UUID, Cable>> clientCables = new HashMap<>();
    private static final Map<Player, Cable> clientPlayersDraggingCables = new HashMap<>();

    public static Map<ResourceKey<Level>, Map<UUID, Cable>> getCablesMap(Level level) {
        return level.isClientSide() ? clientCables : cables;
    }

    public static @Nullable Cable getCable(Level level, UUID id) {
        Map<UUID, Cable> map = getCables(level);
        if (map == null) {
            return null;
        }

        return map.get(id);
    }

    public static @Nullable Map<UUID, Cable> getCables(Level level) {
        return level.isClientSide() ? clientCables.get(level.dimension()) : cables.get(level.dimension());
    }

    public static Map<Player, Cable> getPlayersDraggingCablesMap(Level level) {
        return level.isClientSide() ? playersDraggingCables : clientPlayersDraggingCables;
    }

    private static void syncCable(ServerLevel level, Cable cable) {
        CableSyncS2CPacket packet = new CableSyncS2CPacket(cable);
        Vec3 pos = cable.getPoints().getFirst().getPosition();
        VeilPacketManager.around(null, level, pos.x, pos.y, pos.z, cable.getPoints().size() + 100).sendPacket(packet);
    }

    public static void tick(ServerLevel level) {
        Map<UUID, Cable> cables = getCables(level);
        if (cables != null) {
            for (Cable cable : cables.values()) {
                cable.updatePhysics();
            }
            dragPlayers(level);

            for (Cable cable : cables.values()) {
                syncCable(level, cable);
            }
        }
    }

    public static void clientTick(Level level) {
        if (CableRenderer.detachDelta > 0) {
            CableRenderer.detachDelta = CableRenderer.detachDelta - 0.2f;
        }
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
                        if (longestSegment < cable.getPoints().get(i).getLength()) {
                            longestSegment = cable.getPoints().get(i).getLength();
                        }
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

    public static InteractionResult playerUseEvent(Player player, BlockPos pos, Direction face) {
        if (!player.isShiftKeyDown()) {
            int id = player.getId();
            Level level = player.level();
            for (Cable cable : getLevelCables(level)) {
                if (cable.hasPlayerHolding(id)) {
                    playerFinishDraggingCable(player, pos, face);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        }

        return InteractionResult.PASS;
    }

    public static InteractionResult playerEmptyClickEvent(Player player, Level level) {
        int id = player.getId();
        Map<UUID, Cable> cables = getCables(level);
        if (cables != null) {
            for (Cable cable : cables.values()) {
                if (cable.hasPlayerHolding(id)) {
                    cable.stopPlayerDrag(id);
                    VeilPacketManager.server().sendPacket(PlayerDropCableC2SPacket.INSTANCE);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        }

        CableClipResult cableClipResult = new CableClipResult(player.getEyePosition(), 8, level);
        Pair<Cable, Cable.Point> rayCast = cableClipResult.rayCastForClosest(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(5)), .7f);
        if (rayCast != null) {
            Cable cable = rayCast.getA();
            cable.addPlayerHoldingPoint(id, cable.getPointIndex(rayCast.getB()));
            VeilPacketManager.server().sendPacket(new PlayerGrabCableC2SPacket(cable.getId()));
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    private static void playerStartCable(BlockPos pos, Direction face, Level level, Player player, Color color) {
        if (player.level().isClientSide) {
            return;
        }

        Vec3 anchorPosition = Cable.getAnchoredPoint(pos, face);
        Cable newCable = new Cable(UUID.randomUUID(), anchorPosition, player.getRopeHoldPosition(0), SuperpositionConstants.cableSpawnAmount, level, color);
        newCable.getPoints().getFirst().setAnchor(pos, face);
        newCable.setPlayerHolding(player);
        addCable(newCable, level);
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
                if (player.level() instanceof ServerLevel serverLevel) {
                    syncCable(serverLevel, cable);
                }
            }
        }
    }

    public static void playerExtendsCable(Player player, int amount) {
        for (Cable cable : getLevelCables(player.level())) {
            int id = player.getId();
            if (cable.hasPlayerHolding(id)) {
                int index = cable.getPlayerHeldPoint(id).getB();
                Vec3 pos = player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(3));
                for (int i = 0; i < amount; i++) {
                    cable.addPointAtIndex(index, new Cable.Point(pos));
                }
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

    public static void addCable(Cable cable, Level level) {
        getCablesMap(level).computeIfAbsent(level.dimension(), unused -> new HashMap<>()).put(cable.getId(), cable);
    }

    public static Collection<Cable> getLevelCables(Level level) {
        Map<UUID, Cable> cables = getCables(level);
        return cables != null ? cables.values() : Collections.emptyList();
    }

    public static void removeCable(UUID cableId) {

    }
}
