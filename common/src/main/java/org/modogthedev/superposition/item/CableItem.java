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
import org.modogthedev.superposition.util.SuperpositionConstants;

import java.awt.*;

public class CableItem extends Item {
    private Vec3 start;
    private Vec3 end;
    private Color color;

    public CableItem(Properties properties, Color color) {
        super(properties);
        this.color = color;
    }

    private static final int SIZE = 3;

    @Override
    public InteractionResult useOn(UseOnContext context) {
        CableManager.playerUsesCable(context.getPlayer(), context.getClickedPos(),color, context.getClickedFace());
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        for (Cable cable : CableManager.getLevelCables(level)) {
            if (cable.hasPlayerHolding(player.getId())) {
                if (!player.isShiftKeyDown())
                    CableManager.playerExtendsCable(player, SuperpositionConstants.cableSpawnAmount);
                else
                    CableManager.playerShrinksCable(player);
                return InteractionResultHolder.success(player.getItemInHand(usedHand));
            }
        }
        return super.use(level, player, usedHand);
    }
}
