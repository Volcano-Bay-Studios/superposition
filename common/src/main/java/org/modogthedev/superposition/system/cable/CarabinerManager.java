package org.modogthedev.superposition.system.cable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CarabinerManager {

    private static final Map<UUID, Cable.Point> PLAYER_POINT_MAP = new HashMap<>();

    public static void tick(Level level) {
        for (Map.Entry<UUID, Cable.Point> entry : PLAYER_POINT_MAP.entrySet()) {
            Player player = level.getPlayerByUUID(entry.getKey());
            if (player != null) {
                Cable.Point point = entry.getValue();
                player.setDeltaMovement(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(1)).subtract(0f, 0.2f, 0f).subtract(point.getPosition()).scale(-0.5f));
                if (player.isShiftKeyDown()) {
                    removePlayer(player.getUUID());
                }
            }
        }
    }

    public static void attachPlayer(Player player, Cable.Point point) {
        PLAYER_POINT_MAP.put(player.getUUID(), point);
    }

    public static void removePlayer(UUID uuid) {
        PLAYER_POINT_MAP.remove(uuid);
    }
}
