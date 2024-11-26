package org.modogthedev.superposition.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.modogthedev.superposition.core.SuperpositionBlockStates;
import org.modogthedev.superposition.system.antenna.AntennaManager;
import org.modogthedev.superposition.util.BlockHelper;

public class AntennaBlock extends CrossCollisionBlock {
    public static BooleanProperty SHORT = SuperpositionBlockStates.SHORT;
    public static final MapCodec<IronBarsBlock> CODEC = simpleCodec(IronBarsBlock::new);
    @Override
    protected MapCodec<? extends CrossCollisionBlock> codec() {
        return CODEC;
    }
    public AntennaBlock(Properties p_54198_) {
        super(2F, 1.0F, 16.0F, 16.0F, 16.0F, p_54198_);
        this.registerDefaultState((this.stateDefinition.any()).setValue(SHORT,false));
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        AntennaManager.antennaPartUpdate(pLevel,pPos);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        AntennaManager.antennaPartUpdate(pLevel,pPos);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockGetter blockgetter = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.south();
        BlockPos blockpos3 = blockpos.west();
        BlockPos blockpos4 = blockpos.east();
        BlockState blockstate = blockgetter.getBlockState(blockpos1);
        BlockState blockstate1 = blockgetter.getBlockState(blockpos2);
        BlockState blockstate2 = blockgetter.getBlockState(blockpos3);
        BlockState blockstate3 = blockgetter.getBlockState(blockpos4);
        return this.defaultBlockState().setValue(NORTH, Boolean.valueOf(this.attachsTo(blockstate, blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.SOUTH)))).setValue(SOUTH, Boolean.valueOf(this.attachsTo(blockstate1, blockstate1.isFaceSturdy(blockgetter, blockpos2, Direction.NORTH)))).setValue(WEST, Boolean.valueOf(this.attachsTo(blockstate2, blockstate2.isFaceSturdy(blockgetter, blockpos3, Direction.EAST)))).setValue(EAST, Boolean.valueOf(this.attachsTo(blockstate3, blockstate3.isFaceSturdy(blockgetter, blockpos4, Direction.WEST)))).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
    }

    /**
     * Update the provided state given the provided neighbor direction and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific direction passed in.
     */
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        AntennaManager.antennaPartUpdate(pLevel,pCurrentPos);

        return pFacing.getAxis().isHorizontal() ? pState.setValue(PROPERTY_BY_DIRECTION.get(pFacing), Boolean.valueOf(this.attachsTo(pFacingState, pFacingState.isFaceSturdy(pLevel, pFacingPos, pFacing.getOpposite())))) : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }


    public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }
    public static boolean isCap(BlockState state) {
        boolean north = state.getValue(NORTH);
        boolean south = state.getValue(SOUTH);
        boolean west = state.getValue(WEST);
        boolean east = state.getValue(EAST);
        if (north && !south && !west && !east)
            return true;
        else if (south && !north && !west && !east)
            return true;
        else if (west && !north && !south && !east)
            return true;
        else return east && !north && !south && !west;
    }

    public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
        if (pAdjacentBlockState.is(this)) {
            if (!pSide.getAxis().isHorizontal()) {
                return true;
            }

            if (pState.getValue(PROPERTY_BY_DIRECTION.get(pSide)) && pAdjacentBlockState.getValue(PROPERTY_BY_DIRECTION.get(pSide.getOpposite()))) {
                return true;
            }
        }

        return super.skipRendering(pState, pAdjacentBlockState, pSide);
    }

    public final boolean attachsTo(BlockState pState, boolean pSolidSide) {
        return !isExceptionForConnection(pState) && pSolidSide || pState.getBlock() instanceof AntennaBlock || pState.is(BlockTags.WALLS);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED,SHORT);
    }


}
