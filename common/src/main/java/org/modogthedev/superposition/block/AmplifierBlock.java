package org.modogthedev.superposition.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import org.modogthedev.superposition.blockentity.AmplifierBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionBlockStates;
import org.modogthedev.superposition.screens.AmplifierScreen;
import org.modogthedev.superposition.screens.ScreenManager;
import org.modogthedev.superposition.util.IRedstoneConnectingBlock;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

import java.util.stream.Stream;

public class AmplifierBlock extends SignalActorTickingBlock implements EntityBlock, IRedstoneConnectingBlock {
    public static IntegerProperty BASE_FREQUENCY = SuperpositionBlockStates.FREQUENCY;
    public static AmplifierScreen amplifierScreen = null;
    public static final VoxelShape SHAPE_COMMON = Stream.of(
            Block.box(14, 4, 4, 16, 12, 12),
            Block.box(0, 4, 4, 2, 12, 12),
            Block.box(4, 4, 14, 12, 12, 16),
            Block.box(14, 4, 4, 16, 12, 12),
            Block.box(2, 0, 2, 14, 14, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_NORTH = Stream.of(
            Block.box(14, 4, 4, 16, 12, 12),
            Block.box(0, 4, 4, 2, 12, 12),
            Block.box(4, 4, 14, 12, 12, 16),
            Block.box(14, 4, 4, 16, 12, 12),
            Block.box(2, 0, 2, 14, 14, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_WEST = Stream.of(
            Block.box(4, 4, 0, 12, 12, 2),
            Block.box(4, 4, 14, 12, 12, 16),
            Block.box(14, 4, 4, 16, 12, 12),
            Block.box(4, 4, 0, 12, 12, 2),
            Block.box(2, 0, 2, 14, 14, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_EAST = Stream.of(
            Block.box(4, 4, 14, 12, 12, 16),
            Block.box(4, 4, 0, 12, 12, 2),
            Block.box(0, 4, 4, 2, 12, 12),
            Block.box(4, 4, 14, 12, 12, 16),
            Block.box(2, 0, 2, 14, 14, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_SOUTH = Stream.of(
            Block.box(0, 4, 4, 2, 12, 12),
            Block.box(14, 4, 4, 16, 12, 12),
            Block.box(4, 4, 0, 12, 12, 2),
            Block.box(0, 4, 4, 2, 12, 12),
            Block.box(2, 0, 2, 14, 14, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public AmplifierBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.NORTH).setValue(SWAP_SIDES, true));
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SuperpositionBlockEntities.AMPLIFIER.get().create(pos, state);
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            ScreenManager.openModulatorScreen(pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return direction == state.getValue(FACING);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING, BASE_FREQUENCY, SWAP_SIDES);
    }

    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof AmplifierBlockEntity amplifierBlockEntity) {
            if (amplifierBlockEntity.temp > 40) {
                if ((!pEntity.isSteppingCarefully() || amplifierBlockEntity.temp < 50) && pEntity instanceof LivingEntity) {
                    pEntity.hurt(pLevel.damageSources().hotFloor(), (float) Math.floor(amplifierBlockEntity.temp / 4f) - 9);
                }
            }
        }
        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        AmplifierBlockEntity blockEntity = (AmplifierBlockEntity) pLevel.getBlockEntity(pPos);
        if (blockEntity != null && blockEntity.lastAmplitude > 0) {
            return (int) Math.floor(Math.min(15, blockEntity.lastAmplitude / 10f));
        }
        return 0;
    }
}
