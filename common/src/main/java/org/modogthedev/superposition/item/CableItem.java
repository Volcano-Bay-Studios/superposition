package org.modogthedev.superposition.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableManager;

public class CableItem extends Item {
    private Vec3 start;
    private Vec3 end;
    public CableItem(Properties properties) {
        super(properties);
    }
    private static final int SIZE = 3;
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) {
            CableManager.playerUsesCable(context.getPlayer(), context.getClickLocation());
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (CableManager.playersDraggingCables.containsKey(player)) {
            if (!player.isCrouching())
                CableManager.playerExtendsCable(player,10);
            else
                CableManager.playerShrinksCable(player);
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }
        return super.use(level, player, usedHand);
    }
}
