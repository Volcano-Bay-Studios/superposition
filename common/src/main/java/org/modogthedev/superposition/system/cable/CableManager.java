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
import org.modogthedev.superposition.core.SuperpositionItems;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.util.Mth;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CableManager {
    public static HashMap<Level, List<Cable>> cables = new HashMap<>();
    public static HashMap<Player, Cable> playersDraggingCables = new HashMap<>();

    private static void ifAbsent(Level level) {
        if (!cables.containsKey(level)) {
            cables.put(level, new ArrayList<>());
        }
    }

    public static void tick(ServerLevel level) {
    }

    public static void clientTick(Level level) {
        ifAbsent(level);
        for (Cable cable : cables.get(level)) {
//            cable.debugDraw();
            cable.updatePhysics();
        }
        for (Cable cable : playersDraggingCables.values()) {
            cable.updatePhysics();
        }
        dragPlayers();
    }

    public static void dragPlayers() {
        for (Player player : playersDraggingCables.keySet()) {
            LivingEntity holder = (LivingEntity) player;
            Cable cable = playersDraggingCables.get(player);
            Cable.Point playerPoint = cable.getPoints().get(cable.getPoints().size() - 1);
            Cable.Point lastFreePoint = cable.getPoints().get(cable.getPoints().size() - 2);
            Vec3 pointActual = lastFreePoint.getPosition().subtract(holder.getRopeHoldPosition(0)).add(holder.position());
            float distanceToMove = (float) (playerPoint.getPosition().distanceTo(lastFreePoint.getPrevPosition()) - cable.radius) * cable.elasticity;
            Vec3 normal = pointActual.add(0, 0.1f, 0).subtract(player.position());
            if (distanceToMove > .2f) {
                distanceToMove = Mth.getFromRange(1, .2f, 5, 0, distanceToMove);
                holder.setDeltaMovement(holder.getDeltaMovement().add(normal.scale(distanceToMove).scale(0.1f)));
//                holder.move(MoverType.SELF, );
            }
        }
    }

    public static void playerUsesCable(Player player, Vec3 vec3) {
        if (playersDraggingCables.containsKey(player)) {
            playerFinishDraggingCable(player, vec3);
        } else {
            playerStartCable(vec3, player.level(), player);
        }
    }

    public static EventResult playerUseEvent(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        if (player.getItemInHand(hand).is(Items.AIR)) {
            if (!playersDraggingCables.containsKey(player)) {
                CableClipResult cableClipResult = new CableClipResult(player.position(), 5, player.level());
                List<Pair<Cable, Cable.Point>> raycast = cableClipResult.rayCast(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(5)), 1f);
                if (!raycast.isEmpty()) {
                    for (int i = 0; i < raycast.size(); i++) {
                        Cable cable = raycast.get(i).getA();
                        if (cable.getPoints().get(cable.getPoints().size() - 1) == raycast.get(i).getB()) {
                            cable.setPlayerHolding(player);
                            cable.getPoints().get(cable.getPoints().size() - 1).setPosition(player.position());
                            playersDraggingCables.put(player, cable);
                            cables.get(player.level()).remove(cable);
                            return EventResult.interruptTrue();
                        }
                    }
                }
            } else {
                Cable cable = playersDraggingCables.get(player);
                if (player.isCrouching()) {
                    if (cable.getPoints().size() > 4)
                        cable.shrink();
                }
            }
        }
        return EventResult.interruptDefault();
    }

    private static void playerStartCable(Vec3 pos, Level level, Player player) {
        Cable newCable = new Cable(pos, player.position(), 4, level);
        newCable.setPlayerHolding(player);
        playersDraggingCables.put(player, newCable);
    }

    private static void playerFinishDraggingCable(Player player, Vec3 vec3) {
        Cable cable = playersDraggingCables.get(player);
        if (cable != null) {
            cable.addPoint(new Cable.Point(vec3));
            cable.setPlayerHolding(null);
            playersDraggingCables.remove(player);
            addCable(cable, player.level());
        }
    }

    public static void playerExtendsCable(Player player, int amount) {
        Cable cable = playersDraggingCables.get(player);
        if (cable != null) {
            for (int i = 0; i < amount; i++)
                cable.addPoint(new Cable.Point(player.position()));
        }
    }

    public static void playerShrinksCable(Player player) {
        Cable cable = playersDraggingCables.get(player);
        if (cable != null) {
            cable.shrink();
        }
    }

    public static void addCable(Cable cable, Level level) {
        ifAbsent(level);
        cables.get(level).add(cable);
    }

    public static List<Cable> getLevelCables(Level level) {
        ifAbsent(level);
        return cables.get(level);
    }

    public static List<Cable> getPlayerDraggingCables() {
        return (List<Cable>) playersDraggingCables.values();
    }
}
