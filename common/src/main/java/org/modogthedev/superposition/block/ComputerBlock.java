package org.modogthedev.superposition.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.item.CardItem;
import org.modogthedev.superposition.util.IRedstoneConnectingBlock;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

public class ComputerBlock extends SignalActorTickingBlock implements EntityBlock, IRedstoneConnectingBlock {

    public ComputerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.NORTH).setValue(SWAP_SIDES, true));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_52669_) {
        return this.defaultBlockState().setValue(FACING, p_52669_.getHorizontalDirection().getOpposite());
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
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isCrouching()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ComputerBlockEntity computerBlockEntity && computerBlockEntity.getCard() != null) {
                Item item = BuiltInRegistries.ITEM.get(computerBlockEntity.getCard().getSelfReference());
                if (item != null && item instanceof CardItem cardItem) {
                    cardItem.card = computerBlockEntity.getCard();
                    player.getInventory().add(cardItem.getDefaultInstance().copy());
                    computerBlockEntity.setCard(null);
                }
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(new Property[]{FACING, SWAP_SIDES});
    }

}
