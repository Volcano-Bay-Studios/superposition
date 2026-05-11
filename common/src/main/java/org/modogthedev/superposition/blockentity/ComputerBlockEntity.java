package org.modogthedev.superposition.blockentity;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.blockentity.util.CardHolder;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.card.Card;
import org.modogthedev.superposition.system.card.ExecutableAction;
import org.modogthedev.superposition.system.card.Node;
import org.modogthedev.superposition.system.card.actions.InputAction;
import org.modogthedev.superposition.system.card.actions.OutputAction;
import org.modogthedev.superposition.system.card.actions.configuration.PortConfiguration;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;
import org.modogthedev.superposition.util.SignalHelper;
import org.modogthedev.superposition.util.TickableBlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class ComputerBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity, CardHolder { // TODO: It no longer works (intentional)
    private static final Logger log = LoggerFactory.getLogger(ComputerBlockEntity.class);
    private Card card;
    private boolean appendData = false;

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.COMPUTER.get(), pos, state);
    }

    public static float getRedstoneOffset(Level level, BlockPos pos) {
        return level.getSignal(pos, level.getBlockState(pos).getValue(AmplifierBlock.FACING).getOpposite());
    }

    @Override
    public PortConfig.Builder buildPorts(PortConfig.Builder builder) {
        return builder.addInputPort("reprogram");
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
        rebuild(card);
        sendData();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag cardTag = new CompoundTag();
        if (card != null) {
            card.save(cardTag);
            tag.put("card", cardTag);
        }
        tag.putBoolean("appendData", appendData);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("card")) {
            CompoundTag cardTag = tag.getCompound("card");
            setCard(new Card(cardTag));
        } else {
            setCard(null);
        }
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
    public void setupConfigTooltips(Player player) {
        super.setupConfigTooltips(player);
        this.addConfigTooltip("Append Data - " + appendData, () -> {
            CompoundTag tag = new CompoundTag();
            appendData = !appendData;
            tag.putBoolean("appendData", appendData);
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, this.getBlockPos()));
        });
    }

    public void rebuild(Card card) {
        PortConfig.Builder builder = getPortConfig().rebuild();
        builder.addInputPort("reprogram");

        if (card != null) {
            for (Node node : card.getNodes()) {
                if (node.getAction() instanceof InputAction inputAction && inputAction.getConfigurations().getFirst() instanceof PortConfiguration portConfiguration) {
                    String port = portConfiguration.getString();
                    PortConfig.Port existing = builder.getExisting(port);
                    if (existing != null) {
                        builder.removeExisting(port);
                        builder.addBothPort(port);
                    } else {
                        builder.addInputPort(port);
                    }
                }
                if (node.getAction() instanceof OutputAction outputAction && outputAction.getConfigurations().getFirst() instanceof PortConfiguration portConfiguration) {
                    String port = portConfiguration.getString();
                    PortConfig.Port existing = builder.getExisting(port);
                    if (existing != null) {
                        builder.removeExisting(port);
                        builder.addBothPort(port);
                    } else {
                        builder.addOutputPort(port);
                    }
                }
            }
        }
        builder.build();
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            resetTooltip();
            addTooltip(Component.literal("Computer Status:"));
            if (card == null)
                addTooltip(Component.literal("No Card"));
            else {
                addTooltip(Component.literal("Card Present"));
            }
        }
        if (card != null) {
            for (Node node : card.getNodes()) {
                node.clearForExecution();
            }
            for (Node node : card.getNodes()) {
                if (node.getAction() instanceof InputAction inputAction && inputAction.getConfigurations().getFirst() instanceof PortConfiguration portConfiguration) {
                    List<Signal> signals = getPortSignals(portConfiguration.getString());
                    if (!signals.isEmpty()) {
                        node.execute(signals, level, getBlockPos());
                    }
                } else if (node.getAction() instanceof ExecutableAction action && action.getParameterCount() == 0) {
                    node.execute(SignalHelper.listOf(SignalHelper.getEmptySignal(getLevel(), getBlockPos())), level, getBlockPos());
                }
            }
        }
        super.tick();
    }

    public void addOutbound(String port, List<Signal> signals) {
        getSignalList(port).addAll(signals);
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
    public boolean putPortSignals(String port, List<Signal> signals) {
        if (Objects.equals("reprogram", port)) {
            Signal signal = signals.getLast();
            if (signal != null && signal.getEncodedData() != null && signal.getEncodedData().compoundTagData() != null) {
                CompoundTag tag = signal.getEncodedData().compoundTagData();
                if (card != null) {
                    Card newCard = new Card();
                    CompoundTag load = newCard.load(signal.getEncodedData().compoundTagData());
                    if (load != null) {
                        card = newCard;
                        rebuild(card);
                        markDirty();
                    }
                }
                return true;
            }
        }
        return super.putPortSignals(port, signals);
    }

    @Override
    public @Nullable Signal manipulateSignal(Signal signal) {
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
}


