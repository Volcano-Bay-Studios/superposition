package org.modogthedev.superposition.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SuperpositionItemHelper {
    public static boolean putItem(ItemStack stack, Player player) {
        if (player.getInventory().getItem(player.getInventory().getSuitableHotbarSlot()).isEmpty()) {
            player.getInventory().add(player.getInventory().getSuitableHotbarSlot(), stack);
            return true;
        } else if (player.getInventory().getFreeSlot() >= 0) {
            player.getInventory().add(player.getInventory().getFreeSlot(), stack);
            return true;
        }
        return false;
    }
}
