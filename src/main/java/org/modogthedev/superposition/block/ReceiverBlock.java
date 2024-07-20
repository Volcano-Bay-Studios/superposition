package org.modogthedev.superposition.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.blockentity.ReceiverBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

public class ReceiverBlock extends SignalActorTickingBlock {
    public ReceiverBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.NORTH).setValue(SWAP_SIDES,true));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_52669_) {
        return this.defaultBlockState().setValue(FACING, p_52669_.getHorizontalDirection().getOpposite());
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(new Property[]{FACING, SWAP_SIDES});
    }
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.join(Block.box(4, 14, 4, 12, 16, 12), Block.box(2, 0, 2, 14, 14, 14), BooleanOp.OR);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.join(Block.box(4, 14, 4, 12, 16, 12), Block.box(2, 0, 2, 14, 14, 14), BooleanOp.OR);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SuperpositionBlockEntity.RECEIVER.get().create(pos, state);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        ReceiverBlockEntity blockEntity = (ReceiverBlockEntity) pLevel.getBlockEntity(pPos);
        if (blockEntity != null && blockEntity.getSignals() != null) {
            return Math.min(15, blockEntity.getSignals().size());
        }
        return 0;
    }
}
