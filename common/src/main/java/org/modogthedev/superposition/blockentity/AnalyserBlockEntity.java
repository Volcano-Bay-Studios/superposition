package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.modogthedev.superposition.core.SuperpositionBehaviors;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.core.SuperpositionTags;
import org.modogthedev.superposition.system.behavior.Behavior;
import org.modogthedev.superposition.system.behavior.types.ScanBehavior;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

import java.util.List;
import java.util.Objects;

public class AnalyserBlockEntity extends PeripheralBlockEntity {
    public float distance = 0;
    public int startDistance = 0;
    public int endDistance = 0;
    private Signal signal;
    private String path = "";

    public AnalyserBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.ANALYSER.get(), pos, state);
        signal = new Signal(new Vector3d(), getLevel(), 1f, 1f, 1f);
    }

    @Override
    public PortConfig.Builder buildPorts(PortConfig.Builder builder) {
        return super.buildPorts(builder).addInputPort("narrowPath");
    }

    @Override
    public boolean putPortSignals(String port, List<Signal> signals) {
        if (Objects.equals(port, "narrowPath")) {
            Signal signal = signals.getLast();
            if (signal != null && signal.getEncodedData() != null) {
                String path = signal.getEncodedData().stringValue();
                this.path = path;
            }

            return true;
        }
        return super.putPortSignals(port, signals);
    }

    @Override
    public void tick() {
        resetTooltip();
        updateDistance();
        addTooltip("Analyser Status:");
        addTooltip("Analyzing " + level.getBlockState(getAnalysisPosition()).getBlock().getName().getString() + "...");
        addTooltip("Distance - " + distance + " Blocks");

        signal.clearEncodedData();
        CompoundTag tag = new CompoundTag();
        for (Behavior behavior : SuperpositionBehaviors.behaviors) {
            if (behavior instanceof ScanBehavior scanBehavior) {
                scanBehavior.scan(tag,this,getLevel(),getAnalysisPosition(),level.getBlockState(getAnalysisPosition()));
            }
        }
        signal.encode(tag);
        String[] split = path.split("/");
        for (String pathTarget : split) {
            try {
                EncodedData<?> data = signal.getEncodedData().getTagKey(pathTarget);
                if (!Objects.equals(data.stringValue(), "null")) {
                    signal.setEncodedData(data);
                }
            } catch (Exception ignored) {}
        }
        putPortSignals(outPortName(), List.of(signal));
        path = "";
        super.tick();
    }

    public void updateDistance() {
        int rangeRatio = 0;
        if (level.getBlockState(getBlockPos().relative(getBlockState().getValue(SignalActorTickingBlock.FACING))).is(SuperpositionTags.GLASS_BLOCKS)) {
            rangeRatio = 1;
        }
        for (int i = 1 + (SuperpositionConstants.analyserRange * rangeRatio); i <= SuperpositionConstants.analyserRange + (SuperpositionConstants.analyserRange * rangeRatio); i++) {
            BlockPos pos = getBlockPos().relative(getBlockState().getValue(SignalActorTickingBlock.FACING), i);
            assert level != null;
            if (!level.getBlockState(pos).is(Blocks.AIR) && !level.getBlockState(pos).is(SuperpositionTags.GLASS_BLOCKS)) {
                startDistance = 1 + (SuperpositionConstants.analyserRange * rangeRatio);
                endDistance = SuperpositionConstants.analyserRange + (SuperpositionConstants.analyserRange * rangeRatio);
                distance = i;
                return;
            }
        }
        startDistance = 1 + (SuperpositionConstants.analyserRange * rangeRatio);
        endDistance = SuperpositionConstants.analyserRange + (SuperpositionConstants.analyserRange * rangeRatio);
        distance = endDistance;
    }

    public Direction getFacing() {
        return getBlockState().getValue(SignalActorTickingBlock.FACING);
    }

    public BlockPos getDistancePosition(float distance) {
        return getBlockPos().relative(getBlockState().getValue(SignalActorTickingBlock.FACING), (int) distance);
    }

    public BlockPos getAnalysisPosition() {
        return getDistancePosition(distance);
    }
}
