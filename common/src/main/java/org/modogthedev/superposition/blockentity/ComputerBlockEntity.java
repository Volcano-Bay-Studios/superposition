package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.cards.codecs.TickingCard;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;

public class ComputerBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    private Card card;

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.COMPUTER.get(), pos, state);
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
        card = Card.loadNew(tag);
        if (card != null) {
            card = card.copy();
            card.computerBlockEntity = this;
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
        card.computerBlockEntity = this;
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
            card.computerBlockEntity = this;
        }
    }

    @Override
    public void tick() {
        preTick();
        if (level.isClientSide) {
            resetTooltip();
            if (card == null)
                addTooltip(Component.literal("No Card"));
            else {
                if (level.getBlockEntity(getBlockPos().above()) instanceof PeriphrealBlockEntity) {
                    if (card.peripherialPosition == null) {
                        card.peripherialPosition = getBlockPos().above();
                    }
                    card.timeSincePeriphrealUpdated = 0;
                }
                addTooltip(Component.literal("Computer Status:"));
                addTooltip(Component.literal("Card - ").append(Component.translatable("item.superposition." + getCard().getSelfReference().getPath())));
                if (card.peripherialPosition != null && level.getBlockEntity(card.peripherialPosition) instanceof PeriphrealBlockEntity periphrealBlockEntity) {
                    addTooltip("Peripheral Attached - " + level.getBlockState(card.peripherialPosition).getBlock().getName().getString());
                }
            }
        }
        if (card != null) {
            for (Signal signal : getSignals()) {
                modulateSignal(signal, false);
            }
            if (card instanceof TickingCard tickingCard) {
                tickingCard.tick(getBlockPos(), level, this);
            }
            if (card.timeSincePeriphrealUpdated > 1)
                card.peripherialPosition = null;
            card.timeSincePeriphrealUpdated++;
        }
        super.tick();
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        if (card != null && signal != null) {
            card.modulateSignal(signal);
        } else
            return null;
        return super.modulateSignal(signal, updateTooltip);
    }

    @Override
    public BlockPos getDataPos() {
        return getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getOpposite(), 1);
    }
}


