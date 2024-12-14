package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionCards;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.cards.codecs.TickingCard;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;

public class ComputerBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    private Card card;
    private Signal periphrealSignal;
    public boolean updatedLastTick = false;

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

    public void acceptPeriphrealSignal(Signal signal) {
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
        if (card != null && !level.getBlockState(getBlockPos().above()).is(Blocks.AIR) && level.getBlockEntity(getBlockPos().above()) instanceof PeriphrealBlockEntity periphrealBlockEntity) {
            float frequency = 0; // Put data signal
            if (!getSignals().isEmpty())
                frequency = getSignal().getFrequency();
            Vec3 center = getBlockPos().getCenter();
            Signal periphrealSignal = new Signal(new Vector3d(center.x,center.y,center.z), level, frequency, 1, frequency / 100000);
            periphrealSignal.encode(SuperpositionCards.CARDS.asVanillaRegistry().getId(SuperpositionCards.CARDS.asVanillaRegistry().get(card.getSelfReference()))); // Encode the id of the card for the analyser
            periphrealBlockEntity.putSignalFace(periphrealSignal, Direction.UP);

            Signal fromSignal = periphrealBlockEntity.getSignal();
            if (fromSignal != null && fromSignal.getEncodedData() != null)
                acceptPeriphrealSignal(fromSignal);
        }
        if (card != null) {
            for (Signal signal : getSignals()) {
                modulateSignal(signal, false);
            }
            if (card instanceof TickingCard tickingCard) {
                tickingCard.tick(getBlockPos(), level, this);
            }
        }
        if (!updatedLastTick && periphrealSignal != null) {
            periphrealSignal.clearEncodedData();
        }
        updatedLastTick = false;
        super.tick();
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        if (periphrealSignal != null && periphrealSignal.getEncodedData() != null) {
            signal.setEncodedData(periphrealSignal.getEncodedData());
        }
        if (card != null && !card.requiresPeriphreal()) {
            card.modulateSignal(signal);
        }
        return super.modulateSignal(signal, updateTooltip);
    }

    @Override
    public BlockPos getDataPos() {
        return getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getOpposite(), 1);
    }
}


