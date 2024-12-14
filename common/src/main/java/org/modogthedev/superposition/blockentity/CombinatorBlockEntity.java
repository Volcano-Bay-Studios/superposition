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
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.MathFunction;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CombinatorBlockEntity extends PeriphrealBlockEntity {
    public CombinatorBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.COMBINATOR.get(), pos, state);
    }

    private Types type = Types.ARITHMETIC;
    private Modes mode = Modes.ADD;
    private List<Signal> rightSignals = new ArrayList<>();
    private int rightSignalsReceived = 0;
    private Signal outputSignal;

    public void updateRightSignals(List<Signal> signals) {
        if (rightSignals.size() == signals.size()) {
            for (int i = 0; i < signals.size(); i++) {
                rightSignals.get(i).copy(signals.get(i));
            }
        } else if (rightSignals.size() > signals.size()) {
            ListIterator<Signal> iterator = rightSignals.listIterator();
            while (iterator.hasNext()) {
                int i = iterator.nextIndex();
                Signal signal = iterator.next();
                if (i >= signals.size()) {
                    iterator.remove();
                    continue;
                }
                signal.copy(signals.get(i));
            }
        } else {
            for (int i = 0; i < signals.size(); i++) {
                Signal signal = signals.get(i);
                if (i >= rightSignals.size()) {
                    rightSignals.add(new Signal(signal));
                    continue;
                }
                rightSignals.get(i).copy(signal);
            }
        }
        modulateSignals(rightSignals, true);
    }

    @Override
    public void addSignals(List<Signal> signals, Direction face) {
        this.modulateSignals(signals, true);
        if (face == getInvertedSwappedSide()) {
            if (signalsReceived == 0) {
                this.updatePutSignals(signals);
            } else {
                for (Signal signal : signals) {
                    putSignals.add(new Signal(signal));
                }
                signalsReceived++;
            }
        } else if (face == getSwappedSide()) {
            if (rightSignalsReceived == 0) {
                this.updateRightSignals(signals);
            } else {
                for (Signal signal : signals) {
                    rightSignals.add(new Signal(signal));
                }
                rightSignalsReceived++;
            }
        }
    }

    @Override
    public void putSignalsFace(Object nextCall, List<Signal> signals, Direction face) {
        if (face == getSwappedSide()) {
            updateRightSignals(signals);
        } else {
            updatePutSignals(signals);
        }
    }


    @Override
    public void tick() {
        if (outputSignal == null) {
            outputSignal = new Signal(Mth.convertVec(getBlockPos()), level, SuperpositionConstants.periphrealFrequency, 1, SuperpositionConstants.periphrealFrequency / 100000);
        }
        resetTooltip();
        addTooltip("Combinator Status:");
        if (!putSignals.isEmpty()) {
            if (putSignals.size() == 1) {
                addTooltip("Left - " + (putSignals.getFirst().getEncodedData() != null ? putSignals.getFirst().getEncodedData().stringValue() : "null"));
            } else {
                addTooltip("Left - " + putSignals.size() + " signals");
            }
        }
        if (!rightSignals.isEmpty()) {
            if (rightSignals.size() == 1) {
                addTooltip("Right - " + (rightSignals.getFirst().getEncodedData() != null ? rightSignals.getFirst().getEncodedData().stringValue() : "null"));
            } else {
                addTooltip("Right - " + rightSignals.size() + " signals");
            }
        }
        if (outputSignal.getEncodedData() != null)
            addTooltip("Output - " + outputSignal.getEncodedData().stringValue());
        float value = 0;
        if (mode != null) {
            float[] floats = new float[putSignals.size() + rightSignals.size()];
            List<Signal> signals = new ArrayList<>(putSignals);
            signals.addAll(rightSignals);
            for (int i = 0; i < signals.size(); i++) {
                if (signals.get(i).getEncodedData() != null) {
                    floats[i] = signals.get(i).getEncodedData().floatValue();
                } else {
                    floats[i] = 0;
                }
            }
            value = mode.evaluate(floats);
        }
        outputSignal.encode(value);

        if (rightSignalsReceived == 0) {
            rightSignals.clear();
        }
        rightSignalsReceived = 0;
        super.tick();
    }

    @Override
    public Signal getSideSignal(Direction face) {
        if (face == Direction.UP) {
            return outputSignal;
        }
        return super.getSideSignal(face);
    }

    @Override
    public List<Signal> getSideSignals(Direction face) {
        if (face == Direction.UP) {
            return List.of(outputSignal);
        }
        return super.getSideSignals(face);
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
        if (Modes.values()[index].type != type) {
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
        this.addConfigTooltip("Mode - " + mode.name(), () -> {
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
        }, Types.ARITHMETIC),
        SUBTRACT((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                value -= floats[i];
            }
            return value;
        }, Types.ARITHMETIC),
        MULTIPLY((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                value *= floats[i];
            }
            return value;
        }, Types.ARITHMETIC),
        DIVIDE((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                value /= floats[i];
            }
            return value;
        }, Types.ARITHMETIC),
        SIN((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.sin(f);
            }
            return value;
        }, Types.TRIGONOMETRIC),
        COS((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.cos(f);
            }
            return value;
        }, Types.TRIGONOMETRIC),
        TAN((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.tan(f);
            }
            return value;
        }, Types.TRIGONOMETRIC),
        ASIN((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.asin(f);
            }
            return value;
        }, Types.TRIGONOMETRIC),
        ACOS((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.acos(f);
            }
            return value;
        }, Types.TRIGONOMETRIC),
        ATAN((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.atan(f);
            }
            return value;
        }, Types.TRIGONOMETRIC),
        SQR((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += f * f;
            }
            return value;
        }, Types.SCIENTIFIC),
        ROOT((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += (float) Math.sqrt(f);
            }
            return value;
        }, Types.SCIENTIFIC),
        FACTORIAL((floats) -> {
            float value = 0;
            for (float f : floats) {
                value += 0;
            }
            return value;
        }, Types.SCIENTIFIC),
        EQUAL((floats) -> {
            float value = floats[0];
            for (int i = 1; i < floats.length; i++) {
                if (value != floats[i])
                    return 0f;
            }
            return 1f;
        }, Types.COMPARISON);
        private MathFunction function;
        public Types type;

        Modes(MathFunction function, Types type) {
            this.function = function;
            this.type = type;
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
    }

    public enum Types {
        ARITHMETIC(),
        TRIGONOMETRIC(),
        SCIENTIFIC(),
        COMPARISON();
    }
}
