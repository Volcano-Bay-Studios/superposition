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
import org.joml.Vector3d;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionCards;
import org.modogthedev.superposition.networking.packet.BlockSignalSyncS2CPacket;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.cards.cards.PeripheralCard;
import org.modogthedev.superposition.system.cards.cards.SynchronizedCard;
import org.modogthedev.superposition.system.cards.cards.TickingCard;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.List;

public class ComputerBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    private Card card;
    public Signal periphrealSignal;
    public boolean updatedLastTick = false;
    private CompoundTag outboundTag = new CompoundTag();

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.COMPUTER.get(), pos, state);
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
        card = Card.loadNew(tag);
        if (card != null) {
            card = card.copy();
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
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        card = Card.loadNew(tag);
        if (card != null) {
            card = card.copy();
        }
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
        if (!level.isClientSide && card != null && !level.getBlockState(getBlockPos().above()).is(Blocks.AIR) && level.getBlockEntity(getBlockPos().above()) instanceof PeriphrealBlockEntity periphrealBlockEntity) {
            periphrealBlockEntity.putSignalFace(getOutboundSignal(), Direction.UP);

            Signal fromSignal = periphrealBlockEntity.getSignal();
            if (fromSignal != null && fromSignal.getEncodedData() != null)
                acceptPeripheralSignal(fromSignal);
        }
        if (card != null) {
            for (Signal signal : getSignals()) {
                modulateSignal(signal, false);
            }
            if (card instanceof TickingCard tickingCard) {
                tickingCard.tick(getBlockPos(), level, this);
            }
            if (card instanceof SynchronizedCard) {
                if (periphrealSignal == null) {
                    periphrealSignal = getOutboundSignal();
                    periphrealSignal.clearEncodedData();
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
        }
        periphrealSignal.encode(outboundTag); // Encode the id of the card for the analyser
        return periphrealSignal;
    }

    @Override
    public List<Signal> getSideSignals(Direction face) {
        return List.of(getSideSignal(face));
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
        if (card != null && !(card instanceof PeripheralCard) && !(card instanceof SynchronizedCard)) {
            card.modulateSignal(signal, periphrealSignal);
        } else if (periphrealSignal != null && periphrealSignal.getEncodedData() != null) {
            signal.setEncodedData(periphrealSignal.getEncodedData());
        }
        return super.modulateSignal(signal, updateTooltip);
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
    public BlockPos getDataPos() {
        return getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getOpposite(), 1);
    }
}


