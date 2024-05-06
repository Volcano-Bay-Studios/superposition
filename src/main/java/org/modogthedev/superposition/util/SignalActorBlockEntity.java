package org.modogthedev.superposition.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.system.signal.Signal;

public class SignalActorBlockEntity extends SyncedBlockEntity implements TickableBlockEntity{
    Object lastCall;
    public SignalActorBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    public BlockPos getSwappedPos() {
        BlockPos sidedPos2 = new BlockPos(0, 0, 0);
        if (!this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES)) {
            sidedPos2 = getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getClockWise(), 1);
        } else {
            sidedPos2 = getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getCounterClockWise(), 1);
        }
        return sidedPos2;
    }
    public Direction getSwappedSide() {
        if (!this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES)) {
            return  level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getClockWise();
        } else {
            return  level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getCounterClockWise();
        }
    }
    public Direction getInvertedSwappedSide() {
        if (this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES)) {
            return  level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getClockWise();
        } else {
            return  level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getCounterClockWise();
        }
    }

    public BlockPos getInvertedSwappedPos() {
        BlockPos sidedPos2 = new BlockPos(0, 0, 0);
        if (this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES)) {
            sidedPos2 = getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getClockWise(), 1);
        } else {
            sidedPos2 = getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getCounterClockWise(), 1);
        }
        return sidedPos2;
    }
    public void postSignal(Signal signal) {

    }
    public Signal getSignal(Object nextCall) {
        BlockPos sidedPos = getSwappedPos();
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(sidedPos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity && (lastCall == null || !lastCall.equals(nextCall))) {
            lastCall = nextCall;
            return this.modulateSignal(signalActorBlockEntity.getSignal(nextCall));
        } else {
            return null;
        }
    }
    public Signal modulateSignal(Signal signal) {
        return signal;
    }
    public Signal createSignal(Object nextCall) {
        BlockPos sidedPos = getSwappedPos();
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(sidedPos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity && (lastCall == null || !lastCall.equals(nextCall))) {
            lastCall = nextCall;
            return this.modulateSignal(signalActorBlockEntity.createSignal(nextCall));
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
    }
}
