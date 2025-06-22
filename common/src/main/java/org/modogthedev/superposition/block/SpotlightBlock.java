package org.modogthedev.superposition.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

import java.util.stream.Stream;

public class SpotlightBlock extends SignalActorTickingBlock implements EntityBlock {

    public static final VoxelShape SHAPE_COMMON = Stream.of(
            Block.box(2, 1, 0, 14, 13, 2),
            Block.box(3, 2, 2, 13, 12, 14),
            Block.box(4, 3, 14, 12, 11, 16),
            Block.box(4, 0, 3, 12, 2, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_NORTH = Stream.of(
            Block.box(2, 1, 0, 14, 13, 2),
            Block.box(3, 2, 2, 13, 12, 14),
            Block.box(4, 3, 14, 12, 11, 16),
            Block.box(4, 0, 3, 12, 2, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_WEST = Stream.of(
            Block.box(0, 1, 2, 2, 13, 14),
            Block.box(2, 2, 3, 14, 12, 13),
            Block.box(14, 3, 4, 16, 11, 12),
            Block.box(3, 0, 4, 13, 2, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_EAST = Stream.of(
            Block.box(14, 1, 2, 16, 13, 14),
            Block.box(2, 2, 3, 14, 12, 13),
            Block.box(0, 3, 4, 2, 11, 12),
            Block.box(3, 0, 4, 13, 2, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_SOUTH = Stream.of(
            Block.box(2, 1, 14, 14, 13, 16),
            Block.box(3, 2, 2, 13, 12, 14),
            Block.box(4, 3, 0, 12, 11, 2),
            Block.box(4, 0, 3, 12, 2, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public SpotlightBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.NORTH).setValue(SWAP_SIDES, true));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SuperpositionBlockEntities.SPOTLIGHT.get().create(pos, state);
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
        switch (pState.getValue(FACING)) {
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
        return getShape(pState, pLevel, pPos, pContext);
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING, SWAP_SIDES);
    }

}
