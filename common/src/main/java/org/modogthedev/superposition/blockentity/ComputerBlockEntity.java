package org.modogthedev.superposition.blockentity;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.math.NumberUtils;
import org.joml.Vector3d;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionCards;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.networking.packet.BlockSignalSyncS2CPacket;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.cards.cards.ManipulatorCard;
import org.modogthedev.superposition.system.cards.cards.PeripheralCard;
import org.modogthedev.superposition.system.cards.cards.SynchronizedCard;
import org.modogthedev.superposition.system.cards.cards.TickingCard;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ComputerBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    private static final Logger log = LoggerFactory.getLogger(ComputerBlockEntity.class);
    private Card card;
    public Signal periphrealSignal;
    public boolean updatedLastTick = false;
    private CompoundTag outboundTag = new CompoundTag();
    private boolean appendData = false;

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.COMPUTER.get(), pos, state);
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
        tag.putBoolean("appendData",appendData);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        card = Card.loadNew(tag);
        if (card != null) {
            card = card.copy();
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
    public void setupConfigTooltips() {
        super.setupConfigTooltips();
        this.addConfigTooltip("Append Data - " + appendData, () -> {
            CompoundTag tag = new CompoundTag();
            appendData = !appendData;
            tag.putBoolean("appendData", appendData);
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, this.getBlockPos()));
        });
    }

    public void acceptPeripheralSignal(Signal signal) {
        if (signal != null) {
            if (periphrealSignal == null)
                periphrealSignal = new Signal(signal);
            else
                periphrealSignal.copy(signal);
            updatedLastTick = true;
        }
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            resetTooltip();
            if (card == null)
                addTooltip(Component.literal("No Card"));
            else {
                addTooltip(Component.literal("Computer Status:"));
                addTooltip(Component.literal("Card - ").append(Component.translatable("item.superposition." + getCard().getSelfReference().getPath())));
            }
        }
        if (!level.isClientSide && card != null && !level.getBlockState(getBlockPos().above()).is(Blocks.AIR) && level.getBlockEntity(getBlockPos().above()) instanceof PeripheralBlockEntity peripheralBlockEntity) {
            peripheralBlockEntity.putSignalFace(getOutboundSignal(), Direction.UP);

            Signal fromSignal = peripheralBlockEntity.getSignal();
            if (fromSignal != null && fromSignal.getEncodedData() != null)
                acceptPeripheralSignal(fromSignal);
        }
        if (card != null) {
            if (card instanceof TickingCard tickingCard) {
                tickingCard.tick(getBlockPos(), level, this);
            }
            if (card instanceof SynchronizedCard) {
                if (periphrealSignal == null) {
                    periphrealSignal = getOutboundSignal();
                }
                card.encodeSignal(periphrealSignal);
                updatedLastTick = true;
            }
        }
        if (!level.isClientSide && !updatedLastTick && periphrealSignal != null) {
            periphrealSignal.clearEncodedData();
        }
        if (!level.isClientSide && periphrealSignal != null) {
            VeilPacketManager.around(null, (ServerLevel) level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 200d).sendPacket(new BlockSignalSyncS2CPacket(List.of(periphrealSignal), getBlockPos()));
        }
        updatedLastTick = false;
        super.tick();
    }

    public Signal getOutboundSignal() {
        float frequency = 1; // Put data signal
        if (!getSignals().isEmpty())
            frequency = getSignal().getFrequency();
        Vec3 center = getBlockPos().getCenter();
        Signal periphrealSignal = new Signal(new Vector3d(center.x, center.y, center.z), level, frequency, 1, frequency / 100000);
        if (card != null) {
            outboundTag.putInt("id", SuperpositionCards.CARDS.asVanillaRegistry().getId(SuperpositionCards.CARDS.asVanillaRegistry().get(card.getSelfReference())));
            if (card instanceof ManipulatorCard manipulatorCard) {
                manipulatorCard.addOutbound(outboundTag,this.periphrealSignal);
            }
        }
        periphrealSignal.encode(outboundTag); // Encode the id of the card for the analyser
        return periphrealSignal;
    }

    @Override
    public List<Signal> getSideSignals(Direction face) {
        Signal signal = getSideSignal(face);
        if (signal != null) {
            return List.of(signal);
        }
        return new ArrayList<>();
    }

    @Override
    public Signal getSideSignal(Direction face) {
        if (face == Direction.UP) {
            return getOutboundSignal();
        }
        return super.getSideSignal(face);
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        Signal dataSignal = new Signal(signal);
        if (card != null && !(card instanceof PeripheralCard) && !(card instanceof SynchronizedCard)) {
            card.modulateSignal(dataSignal,periphrealSignal);
        } else if (periphrealSignal != null && periphrealSignal.getEncodedData() != null) {
            dataSignal.setEncodedData(periphrealSignal.getEncodedData());
        }
        if (appendData) {
            appendData(signal,dataSignal);
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
                information.getEncodedData().writeTag(tag, String.valueOf(largestNumber+1));
                toAppend.setEncodedData(EncodedData.of(tag));
            } else {
                EncodedData<?> oldData = toAppend.getEncodedData();
                if (oldData != null) {
                    CompoundTag tag = new CompoundTag();

                    oldData.writeTag(tag,"0");
                    information.getEncodedData().writeTag(tag,"1");
                    toAppend.setEncodedData(EncodedData.of(tag));
                } else {
                    CompoundTag tag = new CompoundTag();
                    information.getEncodedData().writeTag(tag,"0");
                    toAppend.setEncodedData(EncodedData.of(tag));
                }
            }
        }
    }

    @Override
    public void putSignalsFace(Object nextCall, List<Signal> signals, Direction face) {
        if (face == Direction.UP) {
            for (Signal signal : signals) {
                acceptPeripheralSignal(signal);
            }
        } else {
            putSignalList(nextCall, signals);
        }
    }

    @Override
    public void putSignalFace(Signal signal, Direction face) {
        if (face == Direction.UP) {
            acceptPeripheralSignal(signal);
        }
        super.putSignalFace(signal, face);
    }

    @Override
    public void addSignals(Object lastCall, List<Signal> signals, Direction face) {
        if (face == Direction.UP) {
            putSignalsFace(lastCall,signals,face);
            return;
        }
        super.addSignals(lastCall, signals, face);
    }

    @Override
    public BlockPos getDataPos() {
        return getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getOpposite(), 1);
    }
}


