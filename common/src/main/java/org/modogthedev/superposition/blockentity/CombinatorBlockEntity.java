package org.modogthedev.superposition.blockentity;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;
import org.modogthedev.superposition.util.MathFunction;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.List;

public class CombinatorBlockEntity extends SignalActorBlockEntity {
    public CombinatorBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.COMBINATOR.get(), pos, state);
    }

    private Types type = Types.ARITHMETIC;
    private Modes mode = Modes.ADD;
    private Signal outputSignal;

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
        if (outputSignal.getEncodedData() != null)
            addTooltip("Output - " + outputSignal.getEncodedData().stringValue());
        float value = 0;
        if (mode != null) {
            int size = Math.min(signalsA.size(), signalsB.size());
            float[] floats = new float[size * 2];
            for (int i = 0; i < size; i++) {
                floats[i * 2] = signalsA.get(i).getEncodedData().floatValue();
                floats[i * 2 + 1] = signalsB.get(i).getEncodedData().floatValue();
            }
            if (size == 0) {
                if (!signalsA.isEmpty()) {
                    value += signalsA.getLast().getEncodedData().floatValue();
                } else if (!signalsB.isEmpty()) {
                    value += signalsB.getLast().getEncodedData().floatValue();
                }
            }
            value += mode.evaluate(floats);
        }
        outputSignal.encode(value);

        singleSignalOut(outputSignal);
        super.tick();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("type", type.ordinal());
        tag.putInt("mode", mode.ordinal());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("type")) {
            updateType(tag.getInt("type"));
        }
        if (tag.contains("mode")) {
            updateMode(tag.getInt("mode"));
        }
    }

    public void updateType(int i) {
        if (i >= Types.values().length) {
            type = Types.values()[0];
        } else {
            type = Types.values()[i];
        }
    }

    public void updateMode(int index) {
        if (index >= Modes.values().length || Modes.values()[index].type != type) {
            for (int i = 0; i < Modes.values().length; i++) {
                if (Modes.values()[i].type == type) {
                    mode = Modes.values()[i];
                    return;
                }
            }
        } else {
            mode = Modes.values()[index];
        }
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
        Types oldType = type;
        if (tag.contains("type")) {
            updateType(tag.getInt("type"));
        }
        if (tag.contains("mode")) {
            updateMode(tag.getInt("mode"));
        }
        if (oldType != type) {
            updateMode(0);
        }
    }

    @Override
    public void setupConfigTooltips() {
        super.setupConfigTooltips();
        this.addConfigTooltip("Mode - " + mode.displayText, () -> {
            CompoundTag tag = new CompoundTag();
            tag.putInt("mode", mode.ordinal() + 1);
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, this.getBlockPos()));
        });
        this.addConfigTooltip("Type - " + type.name(), () -> {
            CompoundTag tag = new CompoundTag();
            tag.putInt("type", type.ordinal() + 1);
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, this.getBlockPos()));
        });
    }

    public Modes getMode() {
        return mode;
    }

    public Direction getFacing() {
        return getBlockState().getValue(SignalActorTickingBlock.FACING);
    }

    public enum Modes {
        ADD((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                value += floats[i];
            }
            return value;
        }, Types.ARITHMETIC, "+"),
        SUBTRACT((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                value -= floats[i];
            }
            return value;
        }, Types.ARITHMETIC, "-"),
        MULTIPLY((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                value *= floats[i];
            }
            return value;
        }, Types.ARITHMETIC, "x"),
        DIVIDE((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                value /= floats[i];
            }
            return value;
        }, Types.ARITHMETIC, "/"),
        MODULO((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                value %= floats[i];
            }
            return value;
        }, Types.ARITHMETIC, "%"),
        SIN((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.toDegrees(Math.sin(Math.toRadians(f)));
            }
            return value;
        }, Types.TRIGONOMETRIC, "SIN"),
        COS((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.toDegrees(Math.cos(Math.toRadians(f)));
            }
            return value;
        }, Types.TRIGONOMETRIC, "COS"),
        TAN((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.toDegrees(Math.tan(Math.toRadians(f)));
            }
            return value;
        }, Types.TRIGONOMETRIC, "TAN"),
        ASIN((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.toDegrees(Math.asin(Math.toRadians(f)));
            }
            return value;
        }, Types.TRIGONOMETRIC, "ASIN"),
        ACOS((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.toDegrees(Math.acos(Math.toRadians(f)));
            }
            return value;
        }, Types.TRIGONOMETRIC, "ACOS"),
        ATAN((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.toDegrees(Math.atan(Math.toRadians(f)));
            }
            return value;
        }, Types.TRIGONOMETRIC, "ATAN"),
        POW((floats) -> {
            float value = 0;
            for (int i = 0; i < floats.length; i += 2) {
                value += (float) Math.pow(floats[0], floats[1]);
            }
            return value;
        }, Types.SCIENTIFIC, "POW"),
        ROOT((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.sqrt(f);
            }
            return value;
        }, Types.SCIENTIFIC, "ROOT"),
        EQUAL((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                if (value != floats[i])
                    return 0f;
            }
            return 1f;
        }, Types.COMPARISON, "=="),
        NOT_EQUAL((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                if (value == floats[i])
                    return 0f;
            }
            return 1f;
        }, Types.COMPARISON, "!="),
        LESS_THAN((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                if (value >= floats[i])
                    return 0f;
            }
            return 1f;
        }, Types.COMPARISON, "<"),
        GREATER_THAN((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                if (value <= floats[i])
                    return 0f;
            }
            return 1f;
        }, Types.COMPARISON, ">"),
        LESS_THAN_OR_EQUALS((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                if (value > floats[i])
                    return 0f;
            }
            return 1f;
        }, Types.COMPARISON, "<="),
        GREATER_THAN_OR_EQUALS((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                if (value < floats[i])
                    return 0f;
            }
            return 1f;
        }, Types.COMPARISON, ">=");
        private final MathFunction function;
        public Types type;
        private final String displayText;

        Modes(MathFunction function, Types type, String displayText) {
            this.function = function;
            this.type = type;
            this.displayText = displayText;
        }

        public float evaluate(float[] floats) {
            assert floats != null : "Attempted to evaluate null numbers";
            if (floats.length == 0)
                return 0;
            try {
                return function.apply(floats);
            } catch (ArithmeticException arithmeticException) {
                return 0;
            }
        }

        public String getDisplayText() {
            return displayText;
        }
    }

    public enum Types {
        ARITHMETIC(),
        TRIGONOMETRIC(),
        SCIENTIFIC(),
        COMPARISON()
    }
}
