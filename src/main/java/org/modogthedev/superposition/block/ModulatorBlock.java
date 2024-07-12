package org.modogthedev.superposition.block;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.SuperpositionClient;
import org.modogthedev.superposition.blockentity.ModulatorBlockEntity;
import org.modogthedev.superposition.blockentity.ReceiverBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlockStates;
import org.modogthedev.superposition.item.ScrewdriverItem;
import org.modogthedev.superposition.screens.ModulatorScreen;
import org.modogthedev.superposition.screens.ScreenManager;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

public class ModulatorBlock extends SignalActorTickingBlock implements EntityBlock {
    public static IntegerProperty BASE_FREQUENCY = SuperpositionBlockStates.FREQUENCY;
    public static ModulatorScreen modulatorScreen = null;

    public ModulatorBlock(Properties properties) {
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
        return SuperpositionBlockEntity.MODULATOR.get().create(pos, state);
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
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!(pPlayer.getMainHandItem().getItem() instanceof ScrewdriverItem)) {
            if (pLevel.isClientSide) {
                ScreenManager.openModulatorScreen(pPos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return direction == state.getValue(FACING);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(new Property[]{FACING, BASE_FREQUENCY, SWAP_SIDES});
    }

    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof ModulatorBlockEntity modulatorBlockEntity) {
            if (modulatorBlockEntity.temp > 40) {
                if ((!pEntity.isSteppingCarefully() || modulatorBlockEntity.temp < 50) && pEntity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) pEntity)) {
                    pEntity.hurt(pLevel.damageSources().hotFloor(), (float) Math.floor(modulatorBlockEntity.temp / 4f) - 9);
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
        ModulatorBlockEntity blockEntity = (ModulatorBlockEntity) pLevel.getBlockEntity(pPos);
        if (blockEntity != null && blockEntity.lastAmplitude > 0) {
            return (int) Math.min(15, blockEntity.lastAmplitude/10f);
        }
        return 0;
    }
}
