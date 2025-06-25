package org.modogthedev.superposition.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.blockentity.FilterBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionBlockStates;
import org.modogthedev.superposition.item.FilterItem;
import org.modogthedev.superposition.screens.ScreenManager;
import org.modogthedev.superposition.screens.SignalGeneratorScreen;
import org.modogthedev.superposition.util.IRedstoneConnectingBlock;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.SuperpositionItemHelper;

import java.util.stream.Stream;

public class FilterBlock extends SignalActorTickingBlock implements EntityBlock, IRedstoneConnectingBlock {

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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SuperpositionBlockEntities.FILTER.get().create(pos, state);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FilterBlockEntity filterBlockEntity) {
            if (filterBlockEntity.getFilter() != null) {
                if (player.isCrouching()) {
                    BlockPos dropPos = pos.relative(state.getValue(FACING));
                    ItemStack itemStack = filterBlockEntity.getFilter().getItem();
                    ((FilterItem) itemStack.getItem()).putData(itemStack, filterBlockEntity.getFilter());
                    boolean creative = player.getAbilities().instabuild;
                    if (creative) {
                        if (player.getInventory().contains(itemStack)) {
                            ItemStack foundItem = player.getInventory().getItem(player.getInventory().findSlotMatchingItem(itemStack));
                            if (foundItem.getItem() instanceof FilterItem filterItem) {
                                filterItem.putData(foundItem,filterBlockEntity.getFilter());
                            }
                        } else {
                            if (!SuperpositionItemHelper.putItem(itemStack,player)) {
                                Containers.dropItemStack(level, dropPos.getX(), dropPos.getY(), dropPos.getZ(), itemStack);
                            }
                        }
                    } else if (player.getInventory().getFreeSlot() >= 0) {
                        player.getInventory().add(itemStack);
                    } else {
                        Containers.dropItemStack(level, dropPos.getX(), dropPos.getY(), dropPos.getZ(), itemStack);
                    }
                    filterBlockEntity.setFilter(null);
                } else {
                    if (level.isClientSide) {
                        ScreenManager.openFilterScreen(filterBlockEntity.getFilter(), pos, true);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
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
        stateBuilder.add(FACING, BASE_FREQUENCY, SWAP_SIDES);
    }
}