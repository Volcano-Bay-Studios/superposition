package org.modogthedev.superposition.system.cable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.UUID;

public class CarabinerManager {
    private static HashMap<UUID, Cable.Point> playerPointMap = new HashMap<>();
    public static void serverTick(ServerLevel level) {
        tick(level);
    }
    public static void clientTick(Level level) {
        tick(level);
    }
    private static void tick(Level level) {
        for (UUID uuid : playerPointMap.keySet()) {
            Player player = level.getPlayerByUUID(uuid);
            if (player != null) {
                Cable.Point point = playerPointMap.get(uuid);
                player.setDeltaMovement(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(1)).subtract(0f,0.2f,0f).subtract(point.getPosition()).scale(-0.5f));
                if (player.isShiftKeyDown())
                    removePlayer(player.getUUID());
            }
        }
    }

    public static void attachPlayer(Player player, Cable.Point point) {
        playerPointMap.put(player.getUUID(),point);
    }
    public static void removePlayer(UUID uuid) {
        playerPointMap.remove(uuid);
    }
}
