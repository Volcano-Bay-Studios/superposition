package org.modogthedev.superposition.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.blockentity.util.CardHolder;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.item.CardItem;
import org.modogthedev.superposition.util.IRedstoneConnectingBlock;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.SuperpositionItemHelper;

public class ComputerBlock extends SignalActorTickingBlock implements EntityBlock, IRedstoneConnectingBlock {

    public ComputerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.NORTH).setValue(SWAP_SIDES, true));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SuperpositionBlockEntities.COMPUTER.get().create(pos, state);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        if (!level.getBlockState(pos).getValue(SWAP_SIDES)) {
            return direction == level.getBlockState(pos).getValue(FACING).getClockWise();
        } else {
            return direction == level.getBlockState(pos).getValue(FACING).getCounterClockWise();
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CardHolder cardHolder) {
            if (cardHolder.getCard() != null) {
                    BlockPos dropPos = pos.relative(state.getValue(FACING));
                    ItemStack itemStack = cardHolder.getCard().getItem();
                    ((CardItem) itemStack.getItem()).putData(itemStack,cardHolder.getCard().save(new CompoundTag()));
                    boolean creative = player.getAbilities().instabuild;
                    if (creative) {
                        boolean success = SuperpositionItemHelper.putItem(itemStack, player);
                        if (!success) {
                            Containers.dropItemStack(level, dropPos.getX(), dropPos.getY(), dropPos.getZ(), itemStack);
                        }
                    } else if (player.getInventory().getFreeSlot() >= 0) {
                        boolean success = SuperpositionItemHelper.putItem(itemStack, player);
                        if (!success) {
                            Containers.dropItemStack(level, dropPos.getX(), dropPos.getY(), dropPos.getZ(), itemStack);
                        }
                    } else {
                        Containers.dropItemStack(level, dropPos.getX(), dropPos.getY(), dropPos.getZ(), itemStack);
                    }
                    cardHolder.setCard(null);
                    return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING, SWAP_SIDES);
    }

}
