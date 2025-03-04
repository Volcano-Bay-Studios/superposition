package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.EditableTooltip;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.List;

public class ConstantCombinatorBlockEntity extends SignalActorBlockEntity implements EditableTooltip {
    public ConstantCombinatorBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.CONSTANT_COMBINATOR.get(), pos, state);
    }

    private Signal outputSignal;
    private String outputString = "";

    @Override
    public void tick() {
        if (outputSignal == null) {
            outputSignal = new Signal(SuperpositionMth.convertVec(getBlockPos()), level, SuperpositionConstants.periphrealFrequency, 1, SuperpositionConstants.periphrealFrequency / 100000);
        }
        if (outputString != null)
            outputSignal.encode(outputString);
        BlockEntity blockEntity = level.getBlockEntity(getSwappedPos());
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity) {
            signalActorBlockEntity.putSignalsFace(new Object(), List.of(new Signal(outputSignal)), getInvertedSwappedSide());
        }

        resetTooltip();
        addTooltip("Constant Combinator Status:");
        super.tick();
    }

    public String getOutputString() {
        return outputString;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("output", outputString);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("output")) {
            outputString = tag.getString("output");
        }
    }


    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
        if (tag.contains("output")) {
            outputString = tag.getString("output");
        }
    }

    @Override
    public List<Signal> getSignals() {
        return List.of(outputSignal);
    }

    @Override
    public List<Signal> getSideSignals(Direction face) {
        return getSignals();
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        if (signal.getEncodedData() != null) {
            outputString = signal.getEncodedData().stringValue();
        }
        return super.modulateSignal(signal, updateTooltip);
    }


    public Direction getFacing() {
        return getBlockState().getValue(SignalActorTickingBlock.FACING);
    }

    @Override
    public String getText() {
        return outputString;
    }

    @Override
    public void replaceText(String string) {
        outputString = string;
    }
}

