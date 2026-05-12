package org.modogthedev.superposition.system.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;

public class ClientWidgetManager {
    public static void tick(Level level) {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockEntity blockEntity = level.getBlockEntity(blockHitResult.getBlockPos());
            if (blockEntity instanceof PanelBlockEntity panel) {
                panel.hoverCamera(blockHitResult.getLocation().toVector3f(), true);
            }
        }
    }
}
