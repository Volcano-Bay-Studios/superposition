package org.modogthedev.superposition.system.cable;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.system.cable.rope_system.RopeNode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CarabinerManager {

    private static final Map<UUID, RopeNode> PLAYER_POINT_MAP = new HashMap<>();
    private static float slide = 0f;

    public static void tick(Level level) {
        for (Map.Entry<UUID, RopeNode> entry : PLAYER_POINT_MAP.entrySet()) {
            Player player = level.getPlayerByUUID(entry.getKey());
            if (player != null) {
                RopeNode point = entry.getValue();
                player.setDeltaMovement(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(1)).subtract(0f, 0.2f, 0f).subtract(point.getRenderPosition()).scale(-0.5f));
                if (player.isShiftKeyDown()) {
                    removePlayer(player.getUUID());
                }
            }
        }
    }

    public static void clientTick(Level level) {
        for (Map.Entry<UUID, RopeNode> entry : PLAYER_POINT_MAP.entrySet()) {
            Player player = level.getPlayerByUUID(entry.getKey());
            if (player != null) {
                RopeNode point = entry.getValue();
                RopeNode nextPoint = entry.getValue().getNext();
                Vec3 pos = point.getRenderPosition();
                Vec3 cableVector;
                if (nextPoint != null) {
                    cableVector = nextPoint.getRenderPosition().subtract(point.getRenderPosition()).normalize();
                } else {
                    cableVector = point.getLast().getRenderPosition().subtract(point.getRenderPosition()).normalize();
                }
                if (slide > 0) {
                    if (nextPoint != null) {
                        pos = pos.lerp(nextPoint.getRenderPosition(), slide);
                    } else {
                        slide = 0;
                    }
                }
                if (slide < 0 && point.getLast() != null) {
                    if (point.getLast() != null) {
                        pos = pos.lerp(point.getLast().getRenderPosition(), -slide);
                    } else {
                        slide = 0;
                    }
                }
                if (slide > 1) {
                    PLAYER_POINT_MAP.put(entry.getKey(), nextPoint);
                    slide = 0;
                }
                if (slide < -1 && point.getLast() != null) {
                    PLAYER_POINT_MAP.put(entry.getKey(), point.getLast());
                    slide = 0;
                }
                float moveAmount = (float) player.getViewVector(0).dot(cableVector)/5f;
                if (Minecraft.getInstance().options.keyUp.isDown()) {
                    slide += moveAmount;
                }
                if (Minecraft.getInstance().options.keyLeft.isDown()) {
                    slide -= 0.1f-moveAmount;
                }
                if (Minecraft.getInstance().options.keyDown.isDown()) {
                    slide -= moveAmount;
                }
                if (Minecraft.getInstance().options.keyRight.isDown()) {
                    slide += 0.1f-moveAmount;
                }

                player.setDeltaMovement(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(1)).subtract(0f, 0.2f, 0f).subtract(pos).scale(-0.5f));
                if (player.isShiftKeyDown()) {
                    removePlayer(player.getUUID());
                }
            }
        }
    }

    public static void attachPlayer(Player player, RopeNode point) {
        PLAYER_POINT_MAP.put(player.getUUID(), point);
    }

    public static void removePlayer(UUID uuid) {
        PLAYER_POINT_MAP.remove(uuid);
    }
}
