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
    private static float velocity = 0f;

    public static void tick(Level level) {
        for (Map.Entry<UUID, RopeNode> entry : PLAYER_POINT_MAP.entrySet()) {
            Player player = level.getPlayerByUUID(entry.getKey());
            if (player != null) {
                RopeNode point = entry.getValue();
                player.setDeltaMovement(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(1)).subtract(0f, 0.2f, 0f).subtract(point.getPosition()).scale(-0.5f));
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
                velocity = velocity * 0.95f;
                RopeNode point = entry.getValue();
                RopeNode nextPoint = entry.getValue().getNext();
                Vec3 pos = point.getPosition();
                Vec3 cableVector;
                if (nextPoint != null) {
                    cableVector = nextPoint.getPosition().subtract(point.getPosition()).normalize();
                } else {
                    cableVector = point.getLast().getPosition().subtract(point.getPosition()).normalize();
                }
                if (slide > 0.01) {
                    if (nextPoint != null) {
                        pos = pos.lerp(nextPoint.getPosition(), slide);
                    } else {
                        slide = 0;
                        velocity = 0;
                    }
                }
                if (slide < -0.01) {
                    if (point.getLast() != null) {
                        pos = pos.lerp(point.getLast().getPosition(), -slide);
                    } else {
                        slide = 0;
                        velocity = 0;
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
                Vec3 forward = player.calculateViewVector(player.getViewXRot(0), player.getViewYRot(0));
                Vec3 left = player.calculateViewVector(player.getViewXRot(0), player.getViewYRot(0) - 90);
                Vec3 back = player.calculateViewVector(player.getViewXRot(0), player.getViewYRot(0) + 180);
                Vec3 right = player.calculateViewVector(player.getViewXRot(0), player.getViewYRot(0) + 90);
                Vec3 view = new Vec3(0,0,0);
                velocity += (float) (cableVector.y/-10f);
                if (Minecraft.getInstance().options.keyUp.isDown()) {
                    view = view.add(forward);
                }
                if (Minecraft.getInstance().options.keyLeft.isDown()) {
                    view = view.add(left);
                }
                if (Minecraft.getInstance().options.keyDown.isDown()) {
                    view = view.add(back);
                }
                if (Minecraft.getInstance().options.keyRight.isDown()) {
                    view = view.add(right);
                }
                velocity += (float) view.normalize().dot(cableVector) / 20f;
                slide += velocity;

                player.setDeltaMovement(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(1)).subtract(0f, 0.2f, 0f).subtract(pos).scale(-0.5f));
                if (player.isShiftKeyDown()) {
                    removePlayer(player.getUUID());
                    velocity = 0;
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
