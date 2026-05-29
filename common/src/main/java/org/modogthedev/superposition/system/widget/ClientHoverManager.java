package org.modogthedev.superposition.system.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionItems;

public class ClientHoverManager {
    public static void tick(Level level) {
        Minecraft mc = Minecraft.getInstance();
        HitResult hitResult = mc.hitResult;
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockEntity blockEntity = level.getBlockEntity(blockHitResult.getBlockPos());
            if (blockEntity instanceof PanelBlockEntity panel) {
                Vec3 location = blockHitResult.getLocation();
                panel.hoverCamera(new Vector3d(location.x,location.y,location.z), true);
            }
            if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity) {
                if (player.getItemInHand(InteractionHand.MAIN_HAND).is(SuperpositionItems.SCREWDRIVER.get()) || player.getItemInHand(InteractionHand.OFF_HAND).is(SuperpositionItems.SCREWDRIVER.get())) {
                    signalActorBlockEntity.setupConfigTooltips(player);
                    signalActorBlockEntity.finaliseConfigTooltips();
                }
            }
        }
    }
}
