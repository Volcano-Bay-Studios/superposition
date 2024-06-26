package org.modogthedev.superposition.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;

import java.util.ArrayList;
import java.util.List;

public class SignalActorBlockEntity extends SyncedBlockEntity implements TickableBlockEntity{
    public Antenna antenna;
    Object lastCall;
    Object lastCallList;
    List<Signal> putSignals = new ArrayList<>();
    public SignalActorBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    public BlockPos getDataPos() {
        return getSwappedPos();
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
    public Signal getSignal(Object nextCall, boolean selfModulate) {
        BlockPos sidedPos = getSwappedPos();
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(sidedPos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity && (lastCall == null || !lastCall.equals(nextCall))) {
            Signal refSignal;
            List<Signal> signals = getSignalList(nextCall);
            if (signals != null && !signals.isEmpty())
                refSignal = SignalManager.randomSignal(signals);
            else
                refSignal = signalActorBlockEntity.getSignal(nextCall, true);
            lastCall = nextCall;
            if (selfModulate)
                refSignal = this.modulateSignal(refSignal);
            return refSignal;
        } else {
            return null;
        }
    }
    public List<Signal> getSignalList(Object nextCall) {
        if (!(lastCallList == null || !lastCallList.equals(nextCall))) {
            return null;
        }
        BlockPos sidedPos = getSwappedPos();
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(sidedPos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity) {
            lastCallList = nextCall;
            if (signalActorBlockEntity instanceof AntennaActorBlockEntity antennaActorBlockEntity && antennaActorBlockEntity.antenna == null)
                antennaActorBlockEntity.updateAntenna();
            if (signalActorBlockEntity.antenna != null && signalActorBlockEntity.antenna.signals != null)
                return modulateSignals(signalActorBlockEntity.antenna.signals);
            else
                return modulateSignals(signalActorBlockEntity.getSignalList(nextCall));
        }
        return null;
    }
    public void putSignalList(Object nextCall, List<Signal> list) {
        if (!(lastCallList == null || !lastCallList.equals(nextCall))) {
            return;
        }
        BlockPos sidedPos = getInvertedSwappedPos();
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(sidedPos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity) {
            lastCallList = nextCall;
            putSignals = list;
            signalActorBlockEntity.putSignalList(nextCall, list);
        }
    }
    public List<Signal> modulateSignals(List<Signal> signalList) {
        for (Signal signal: signalList) {
            signal = this.modulateSignal(signal);
        }
        return signalList;
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
    public SignalActorBlockEntity topBE(Object nextCall) {
        BlockPos sidedPos = getSwappedPos();
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(sidedPos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity && (lastCall == null || !lastCall.equals(nextCall))) {
            lastCall = nextCall;
            return signalActorBlockEntity.topBE(nextCall);
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
    }
}
