package org.modogthedev.superposition.blockentity;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.math.NumberUtils;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.system.signal.data.EncodedData;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComputerBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity { // TODO: It no longer works (intentional)
    private static final Logger log = LoggerFactory.getLogger(ComputerBlockEntity.class);
    private Card card;
    private boolean appendData = false;
    private HashMap<Direction, List<Signal>> inboundSignals = new HashMap<>();
    private HashMap<Direction, List<Signal>> outboundSignals = new HashMap<>();

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.COMPUTER.get(), pos, state);

        for (Direction direction : Direction.values()) {
            inboundSignals.put(direction, new ArrayList<>());
            outboundSignals.put(direction, new ArrayList<>());
        }
    }

    public static float getRedstoneOffset(Level level, BlockPos pos) {
        return level.getSignal(pos, level.getBlockState(pos).getValue(AmplifierBlock.FACING).getOpposite());
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
        sendData();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (card != null)
            card.save(tag);
        tag.putBoolean("appendData", appendData);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        card = new Card(tag);
        if (tag.contains("appendData")) {
            appendData = tag.getBoolean("appendData");
        }
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
        if (tag.contains("appendData")) {
            appendData = tag.getBoolean("appendData");
        }
    }

    @Override
    public void setupConfigTooltips() {
        super.setupConfigTooltips();
        this.addConfigTooltip("Append Data - " + appendData, () -> {
            CompoundTag tag = new CompoundTag();
            appendData = !appendData;
            tag.putBoolean("appendData", appendData);
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, this.getBlockPos()));
        });
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            resetTooltip();
            if (card == null)
                addTooltip(Component.literal("No Card"));
            else {
                addTooltip(Component.literal("Computer Status:"));
            }
        }
        super.tick();
    }

//    public Signal getOutboundSignal() {
//        float frequency = 1; // Put data signal
//        if (!getSignals().isEmpty())
//            frequency = getSignal().getFrequency();
//        Vec3 center = getBlockPos().getCenter();
//        Signal periphrealSignal = new Signal(new Vector3d(center.x, center.y, center.z), level, frequency, 1, frequency / 100000);
//        if (card != null) {
//        }
//        periphrealSignal.encode(outboundTag); // Encode the id of the card for the analyser
//        return periphrealSignal;
//    }

    @Override
    public List<Signal> getSideSignals(Direction face) {
        return outboundSignals.get(face);
    }

    @Override
    public Signal getSideSignal(Direction face) {
        return SignalManager.randomSignal(getSideSignals(face));
    }

    //    @Override
//    public Signal getSideSignal(Direction face) {
//        if (face == Direction.UP) {
//            return getOutboundSignal();
//        }
//        return super.getSideSignal(face);
//    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        Signal dataSignal = new Signal(signal);
        if (card != null) {


        }
        if (appendData) {
            appendData(signal, dataSignal);
        } else {
            signal.setEncodedData(dataSignal.getEncodedData());
        }
        return signal;
    }

    public void appendData(Signal toAppend, Signal information) {
        if (toAppend != null && information != null && information.getEncodedData() != null) {
            if (toAppend.getEncodedData() != null && toAppend.getEncodedData().compoundTagData() != null) {
                CompoundTag tag = toAppend.getEncodedData().compoundTagData();
                int largestNumber = -1;
                for (String s : tag.getAllKeys()) { // Evaluate a key
                    try {
                        int number = (int) NumberUtils.createNumber(s);
                        if (number > largestNumber) {
                            largestNumber = number;
                        }
                    } catch (NumberFormatException | StringIndexOutOfBoundsException ignored) {
                    }
                }
                information.getEncodedData().writeTag(tag, String.valueOf(largestNumber + 1));
                toAppend.setEncodedData(EncodedData.of(tag));
            } else {
                EncodedData<?> oldData = toAppend.getEncodedData();
                if (oldData != null) {
                    CompoundTag tag = new CompoundTag();

                    oldData.writeTag(tag, "0");
                    information.getEncodedData().writeTag(tag, "1");
                    toAppend.setEncodedData(EncodedData.of(tag));
                } else {
                    CompoundTag tag = new CompoundTag();
                    information.getEncodedData().writeTag(tag, "0");
                    toAppend.setEncodedData(EncodedData.of(tag));
                }
            }
        }
    }

    @Override
    public void putSignalsFace(Object nextCall, List<Signal> signals, Direction face) {
        inboundSignals.get(face).addAll(signals);
    }

    @Override
    public void putSignalFace(Signal signal, Direction face) {
        inboundSignals.get(face).add(signal);
    }

    @Override
    public void addSignals(Object lastCall, List<Signal> signals, Direction face) {
        putSignalsFace(lastCall, signals, face);
    }
}


