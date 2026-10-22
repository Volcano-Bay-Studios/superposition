package org.modogthedev.superposition.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;

/**
 * A flat panel that is mounted on the floor, a wall, or the ceiling.
 */
public class MountedPanelBlock extends WidgetPanelBlock {
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    public static final int THICKNESS = 2;

    private static final VoxelShape FLOOR = Block.box(0, 0, 0, 16, THICKNESS, 16);
    private static final VoxelShape CEILING = Block.box(0, 16 - THICKNESS, 0, 16, 16, 16);
    private static final VoxelShape NORTH = Block.box(0, 0, 16 - THICKNESS, 16, 16, 16);
    private static final VoxelShape SOUTH = Block.box(0, 0, 0, 16, 16, THICKNESS);
    private static final VoxelShape WEST = Block.box(16 - THICKNESS, 0, 0, 16, 16, 16);
    private static final VoxelShape EAST = Block.box(0, 0, 0, THICKNESS, 16, 16);

    public MountedPanelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.NORTH).setValue(SWAP_SIDES, true).setValue(FACE, AttachFace.FLOOR));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SuperpositionBlockEntities.MOUNTED_PANEL.get().create(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clicked = context.getClickedFace();
        if (clicked.getAxis() == Direction.Axis.Y) {
            return this.defaultBlockState()
                    .setValue(FACE, clicked == Direction.UP ? AttachFace.FLOOR : AttachFace.CEILING)
                    .setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
        return this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, clicked);
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
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACE)) {
            case FLOOR -> FLOOR;
            case CEILING -> CEILING;
            case WALL -> switch (state.getValue(FACING)) {
                case SOUTH -> SOUTH;
                case WEST -> WEST;
                case EAST -> EAST;
                default -> NORTH;
            };
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING, SWAP_SIDES, FACE);
    }
}
