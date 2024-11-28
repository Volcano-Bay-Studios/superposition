package org.modogthedev.superposition.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionBlockStates;
import org.modogthedev.superposition.item.ScrewdriverItem;
import org.modogthedev.superposition.screens.ScreenManager;
import org.modogthedev.superposition.screens.SignalGeneratorScreen;
import org.modogthedev.superposition.util.IRedstoneConnectingBlock;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

public class SignalGeneratorBlock extends SignalActorTickingBlock implements EntityBlock, IRedstoneConnectingBlock {

    public static IntegerProperty BASE_FREQUENCY = SuperpositionBlockStates.FREQUENCY;
    public static BooleanProperty ON = SuperpositionBlockStates.ON;
    public static SignalGeneratorScreen signalGeneratorScreen = null;

    public SignalGeneratorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.NORTH).setValue(SWAP_SIDES, true).setValue(ON, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SuperpositionBlockEntities.SIGNAL_GENERATOR.get().create(pos, state);
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
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (!level.getBlockState(pos).getValue(SWAP_SIDES)) {
            return direction == level.getBlockState(pos).getValue(FACING).getClockWise(); // FIXME
        } else {
            return direction == level.getBlockState(pos).getValue(FACING).getCounterClockWise(); // FIXME
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            ScreenManager.openSignalGenerator(pos);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(new Property[]{FACING, BASE_FREQUENCY, SWAP_SIDES, ON});
    }

}
