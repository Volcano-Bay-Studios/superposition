package org.modogthedev.superposition.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.blockentity.WidgetPanelBlockEntity;
import org.modogthedev.superposition.util.WidgetUseResult;
import org.modogthedev.superposition.util.block.SignalActorTickingBlock;

/**
 * A block that holds widgets, which are interacted with through the block.
 */
public abstract class WidgetPanelBlock extends SignalActorTickingBlock {
    public static int pressing = -1;

    public WidgetPanelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @org.jspecify.annotations.Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!player.isCreative() && level.getBlockEntity(pos) instanceof WidgetPanelBlockEntity panel) {
            panel.dropOnRemove();
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof WidgetPanelBlockEntity panel && !newState.is(this)) {
            panel.dropOnRemove();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof WidgetPanelBlockEntity panel && player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            Vec3 location = hitResult.getLocation();
            WidgetUseResult widgetUseResult = panel.secondaryInteract(player.isShiftKeyDown(), new Vector3d(location.x,location.y,location.z));
            if (widgetUseResult.consumesAction()) {
                pressing = widgetUseResult.getConsumeTime();
                return InteractionResult.CONSUME;
            }
        }
        pressing = -1;
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
