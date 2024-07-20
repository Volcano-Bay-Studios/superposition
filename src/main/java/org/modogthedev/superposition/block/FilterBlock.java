package org.modogthedev.superposition.block;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.blockentity.ReceiverBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlockStates;
import org.modogthedev.superposition.screens.SignalGeneratorScreen;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

import java.util.stream.Stream;

public class FilterBlock extends SignalActorTickingBlock implements EntityBlock {

    public static IntegerProperty BASE_FREQUENCY = SuperpositionBlockStates.FREQUENCY;
    public static SignalGeneratorScreen signalGeneratorScreen = null;

    public static final VoxelShape SHAPE_COMMON = Stream.of(
            Block.box(2, 0, 2, 14, 14, 14),
            Block.box(0, 4, 4, 2, 12, 12),
            Block.box(14, 4, 4, 16, 12, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_NORTH = Stream.of(
            Block.box(2, 0, 2, 14, 14, 14),
            Block.box(0, 4, 4, 2, 12, 12),
            Block.box(14, 4, 4, 16, 12, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_WEST = Stream.of(
            Block.box(2, 0, 2, 14, 14, 14),
            Block.box(4, 4, 0, 12, 12, 2),
            Block.box(4, 4, 14, 12, 12, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_EAST = Stream.of(
            Block.box(2, 0, 2, 14, 14, 14),
            Block.box(4, 4, 0, 12, 12, 2),
            Block.box(4, 4, 14, 12, 12, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_SOUTH = Stream.of(
            Block.box(2, 0, 2, 14, 14, 14),
            Block.box(0, 4, 4, 2, 12, 12),
            Block.box(14, 4, 4, 16, 12, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public FilterBlock(Properties properties) {
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
        return SuperpositionBlockEntity.FILTER.get().create(pos, state);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        switch ((Direction)pState.getValue(FACING)) {
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            default:
                return SHAPE_COMMON;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShape(pState,pLevel,pPos,pContext);
    }

    @Override
    public boolean hasDynamicShape() {
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
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
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        return 1;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(new Property[]{FACING, BASE_FREQUENCY, SWAP_SIDES});
    }
}