package org.modogthedev.superposition.system.cable;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.SuperpositionConstants;
import org.modogthedev.superposition.util.Vec3LerpComponent;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CableManager {
    private static final HashMap<Level, List<Cable>> cables = new HashMap<>();
    private static final HashMap<Player, Cable> playersDraggingCables = new HashMap<>();
    private static final HashMap<Level, List<Cable>> clientCables = new HashMap<>();
    private static final HashMap<Player, Cable> clientPlayersDraggingCables = new HashMap<>();

    private static void ifAbsent(Level level) {
        if (!getCables(level).containsKey(level)) {
            getCables(level).put(level, new ArrayList<>());
        }
    }

    public static HashMap<Level, List<Cable>> getCables(Level level) {
        return level.isClientSide ? clientCables : cables;
    }

    public static HashMap<Player, Cable> getPlayersDraggingCables(Level level) {
        return level.isClientSide ? playersDraggingCables : clientPlayersDraggingCables;
    }

    public static void tick(ServerLevel level) {
        ifAbsent(level);
        for (Cable cable : getCables(level).get(level)) {
            cable.updatePhysics();
        }
        for (Cable cable : getPlayersDraggingCables(level).values()) {
            cable.updatePhysics();
        }
        dragPlayers(level);
    }

    public static void clientTick(Level level) {
        ifAbsent(level);
        for (Cable cable : getCables(level).get(level)) {
//            cable.debugDraw();
            cable.updatePhysics();
        }
        for (Cable cable : getPlayersDraggingCables(level).values()) {
            cable.updatePhysics();
        }
        dragPlayers(level);
    }

    public static void dragPlayers(Level level) {
        for (Player player : getPlayersDraggingCables(level).keySet()) {
            LivingEntity holder = (LivingEntity) player;
            Cable cable = getPlayersDraggingCables(level).get(player);
            float longestSegment = 0f;
            for (int i = 1; i < (cable.getPoints().size()); i++) {
                if (longestSegment < cable.getPoints().get(i).getLength())
                    longestSegment = cable.getPoints().get(i).getLength();
            }
            Cable.Point playerPoint = cable.getPoints().get(cable.getPoints().size() - 1);
            Cable.Point lastFreePoint = cable.getPoints().get(cable.getPoints().size() - 2);
            Vec3 pointActual = lastFreePoint.getPosition().subtract(holder.getRopeHoldPosition(0)).add(holder.position());
            float cableDistanceToMove = (float) (longestSegment - cable.radius) * cable.elasticity;
            float distanceToMove = (float) (playerPoint.getPosition().distanceTo(lastFreePoint.getPrevPosition()) - cable.radius) * cable.elasticity;
            Vec3 normal = pointActual.add(0, 0, 0).subtract(player.position()).normalize();
            longestSegment = Math.max(1, Mth.getFromRange(1, 0, 1, 0, longestSegment));
            longestSegment = (float) Math.pow(longestSegment / 4, 3f);
            if (longestSegment > .1f) {
                longestSegment = net.minecraft.util.Mth.clamp(longestSegment, 2.5f, 4);
                Vec3 toAdd = normal.scale(longestSegment).scale(.1f);
                holder.setDeltaMovement(holder.getDeltaMovement().subtract(cable.playerDraggedLastDelta.scale(0.5f)).add(toAdd));
                cable.playerDraggedLastDelta = toAdd;
            }
        }
    }

    public static void playerUsesCable(Player player, Vec3 vec3) {
        if (getPlayersDraggingCables(player.level()).containsKey(player)) {
            playerFinishDraggingCable(player, vec3);
        } else {
            playerStartCable(vec3, player.level(), player);
        }
    }

    public static EventResult playerUseEvent(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        Vec3 anchorPosition = pos.getCenter().add(pos.getCenter().subtract(pos.relative(face).getCenter()).scale(-0.45));
        if (player.getItemInHand(hand).is(Items.AIR)) {
            if (!getPlayersDraggingCables(player.level()).containsKey(player)) {
                CableClipResult cableClipResult = new CableClipResult(player.position(), 8, player.level());
                List<Pair<Cable, Cable.Point>> raycast = cableClipResult.rayCast(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(5)), .7f);
                if (!raycast.isEmpty()) {
                    for (int i = 0; i < raycast.size(); i++) {
                        Cable cable = raycast.get(i).getA();
                        if (cable.getPoints().get(cable.getPoints().size() - 1) == raycast.get(i).getB()) {
                            cable.setPlayerHolding(player);
                            cable.getPoints().get(cable.getPoints().size() - 1).setPosition(player.position());
                            getPlayersDraggingCables(player.level()).put(player, cable);
                            getCables(player.level()).get(player.level()).remove(cable);
                            return EventResult.interruptTrue();
                        }
                    }
                }
            } else {
                Cable cable = getPlayersDraggingCables(player.level()).get(player);
                if (player.isCrouching()) {
                    playerFinishDraggingCable(player, anchorPosition);
                    return EventResult.interruptTrue();
                }
            }
        }
        return EventResult.interruptDefault();
    }

    private static void playerStartCable(Vec3 pos, Level level, Player player) {
        Cable newCable = new Cable(pos, player.getRopeHoldPosition(0), SuperpositionConstants.cableSpawnAmount, level);
        newCable.setPlayerHolding(player);
        getPlayersDraggingCables(level).put(player, newCable);
    }

    public static void playerFinishDraggingCable(Player player, Vec3 vec3) {
        Cable cable = getPlayersDraggingCables(player.level()).get(player);
        if (cable != null) {
            Cable.Point anchorPoint = new Cable.Point(vec3);
            anchorPoint.lerpedPos = new Vec3LerpComponent(vec3, cable.getPoints().get(cable.getPoints().size() - 1).getPosition(), 5);
            cable.addPoint(anchorPoint);
            cable.setPlayerHolding(null);
            getPlayersDraggingCables(player.level()).remove(player);
            addCable(cable, player.level());
        }
    }

    public static void playerExtendsCable(Player player, int amount) {
        Cable cable = getPlayersDraggingCables(player.level()).get(player);
        if (cable != null) {
            for (int i = 0; i < amount; i++)
                cable.addPoint(new Cable.Point(player.position()));
        }
    }

    public static void playerShrinksCable(Player player) {
        Cable cable = getPlayersDraggingCables(player.level()).get(player);
        if (cable != null) {
            cable.shrink();
        }
    }

    public static void addCable(Cable cable, Level level) {
        ifAbsent(level);
        getCables(level).get(level).add(cable);
    }

    public static List<Cable> getLevelCables(Level level) {
        ifAbsent(level);
        return getCables(level).get(level);
    }

    public static List<Cable> getPlayerDraggingCables(Level level) {
        return (List<Cable>) getPlayersDraggingCables(level).values();
    }
}
