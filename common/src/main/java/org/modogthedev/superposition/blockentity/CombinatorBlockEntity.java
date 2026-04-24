package org.modogthedev.superposition.blockentity;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.client.renderer.ui.SuperpositionUITooltipRenderer;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;
import org.modogthedev.superposition.util.EditableTooltip;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.List;
import java.util.Objects;

public class CombinatorBlockEntity extends SignalActorBlockEntity implements EditableTooltip {
    public CombinatorBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.COMBINATOR.get(), pos, state);
    }

    private Signal outputSignal;
    private Expression expression = null;

    private String expressionString = "";
    private String lastString = "";

    @Override
    public PortConfig.Builder buildPorts(PortConfig.Builder builder) {
        return builder.addInputPort("a").addInputPort("b").addOutputPort(outPortName());
    }

    @Override
    public void tick() {
        if (outputSignal == null) {
            outputSignal = new Signal(SuperpositionMth.convertVec(getBlockPos()), level, SuperpositionConstants.periphrealFrequency, 1, SuperpositionConstants.periphrealFrequency / 100000);
        }
        resetTooltip();
        addTooltip("Combinator Status:");
        List<Signal> signalsA = getPortSignals("a");
        if (!signalsA.isEmpty()) {
            if (signalsA.size() == 1) {
                EncodedData<?> signalA = signalsA.getFirst().getEncodedData();
                addTooltip("A - " + (signalA != null ? signalA.stringValue() : "null"));
            } else {
                addTooltip("A - " + signalsA.size() + " signals");
            }
        }
        List<Signal> signalsB = getPortSignals("b");
        if (!signalsB.isEmpty()) {
            if (signalsB.size() == 1) {
                EncodedData<?> signalB = signalsB.getFirst().getEncodedData();
                addTooltip("Right - " + (signalB != null ? signalB.stringValue() : "null"));
            } else {
                addTooltip("Right - " + signalsB.size() + " signals");
            }
        }
        float value;
        if (!Objects.equals(expressionString, lastString)) {
            expression = new Expression(expressionString);
            lastString = expressionString;
        }
        try {
            Expression expressionToEval = expression.copy();
            if (!signalsA.isEmpty()) {
                EncodedData<?> encodedData = signalsA.getFirst().getEncodedData();
                if (encodedData != null) {
                    encodedData.asExpressionVariable("a", expressionToEval);
                }
                for (int i = 0; i < signalsA.size(); i++) {
                    EncodedData<?> thisData = signalsA.get(i).getEncodedData();
                    if (thisData != null) {
                        thisData.asExpressionVariable("a"+i,expressionToEval);
                    }
                }
            }
            if (!signalsB.isEmpty()) {
                EncodedData<?> encodedData = signalsB.getFirst().getEncodedData();
                if (encodedData != null) {
                    encodedData.asExpressionVariable("b", expressionToEval);
                }

                for (int i = 0; i < signalsB.size(); i++) {
                    EncodedData<?> thisData = signalsB.get(i).getEncodedData();
                    if (thisData != null) {
                        thisData.asExpressionVariable("b"+i,expressionToEval);
                    }
                }
            }

            EvaluationValue evaluation = expressionToEval.evaluate();
            value = evaluation.getNumberValue().floatValue();
        } catch (Exception ignored) {
            value = 0;
        }

        outputSignal.encode(value);

        if (outputSignal.getEncodedData() != null)
            addTooltip("Output - " + outputSignal.getEncodedData().stringValue());
        singleSignalOut(outputSignal);
        super.tick();
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("output", expressionString);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (level != null && level.isClientSide && getBlockPos().equals(SuperpositionUITooltipRenderer.editPos)) {
            return;
        }
        if (tag.contains("output")) {
            expressionString = tag.getString("output");
        }
    }


    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
        if (tag.contains("output")) {
            expressionString = tag.getString("output");
        }
        if (tag.contains("modifiedPosition")) {
            int position = tag.getInt("modifiedPosition");
            String word = tag.getString("changedChar");
            expressionString = (expressionString.substring(0, position) + (word) + expressionString.substring(Math.min(position, expressionString.length())));
        }
    }

    public String getExpressionString() {
        return expressionString;
    }

    @Override
    public void setupConfigTooltips() {
        super.setupConfigTooltips();
    }

    public Direction getFacing() {
        return getBlockState().getValue(SignalActorTickingBlock.FACING);
    }

    @Override
    public String getText() {
        return expressionString;
    }

    @Override
    public void replaceText(String string) {
        this.expressionString = string;
    }
}
