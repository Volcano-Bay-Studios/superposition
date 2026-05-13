package org.modogthedev.superposition.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionBlocks;
import org.modogthedev.superposition.util.DelegateVoxelShape;
import org.modogthedev.superposition.util.DynamicShapedBlockEntity;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

import java.util.ArrayList;
import java.util.List;

public class PanelBlock extends SignalActorTickingBlock implements EntityBlock {

    public PanelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.NORTH).setValue(SWAP_SIDES, true));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SuperpositionBlockEntities.PANEL.get().create(pos, state);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    public void exploreShapes(BlockGetter level, BlockPos pos, BlockPos offset, List<VoxelShape> shapes, Direction dir) {
        if (level.getBlockState(pos).is(SuperpositionBlocks.PANEL.get())) {
            shapes.add(Block.box(0,0,0, 16, 6, 16).move(offset.getX(),offset.getY(),offset.getZ()));
            BlockPos relative = pos.relative(dir);
            exploreShapes(level, relative, relative.subtract(pos).offset(offset), shapes, dir);
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @org.jspecify.annotations.Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!player.isCreative() && level.getBlockEntity(pos) instanceof PanelBlockEntity panel) {
            panel.dropOnRemove();
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof PanelBlockEntity panel) {
            panel.dropOnRemove();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape ourShape = Block.box(0,0,0, 16, 6, 16);
        Direction dir = state.getValue(FACING).getClockWise();
        BlockPos forward = pos.relative(dir);
        BlockPos back = pos.relative(dir.getOpposite());

        List<VoxelShape> shapes = new ArrayList<>();

        exploreShapes(level, forward, forward.subtract(pos), shapes, dir);
        exploreShapes(level, back, back.subtract(pos), shapes, dir.getOpposite());

        for (VoxelShape shape : shapes) {
            ourShape = Shapes.join(ourShape,shape, BooleanOp.OR);
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DynamicShapedBlockEntity dynamicShapedBlockEntity) {
            DelegateVoxelShape delegate = (DelegateVoxelShape) ourShape;
            delegate.setDynamicShape(dynamicShapedBlockEntity);
        }
        return ourShape;
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return super.getInteractionShape(state, level, pos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShape(pState,pLevel,pPos,pContext);
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
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
