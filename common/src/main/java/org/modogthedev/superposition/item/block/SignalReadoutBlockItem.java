package org.modogthedev.superposition.item.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.blockentity.SignalReadoutBlockEntity;
import org.modogthedev.superposition.util.SignalActorBlockEntity;

import java.util.Objects;

public class SignalReadoutBlockItem extends BlockItem {
    public SignalReadoutBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        if (Objects.requireNonNull(pContext.getPlayer()).isCrouching()) {
            if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof SignalActorBlockEntity signalActorBlockEntity) {
                CompoundTag tag = new CompoundTag();
                tag.putInt("x",pContext.getClickedPos().getX());
                tag.putInt("y",pContext.getClickedPos().getY());
                tag.putInt("z",pContext.getClickedPos().getZ());
                pContext.getItemInHand().addTagElement("linkedpos",tag);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(pContext);
    }


    @Override
    public @NotNull InteractionResult place(@NotNull BlockPlaceContext pContext) {
        return super.place(pContext);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
        if (pLevel.getBlockEntity(pPos)  instanceof SignalReadoutBlockEntity signalReadoutBlockEntity) {
            CompoundTag tag = pStack.getTagElement("linkedpos");
            if (tag != null) {
                signalReadoutBlockEntity.loadLinkedPos(tag);
            }
        }
        return super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
    }
}
