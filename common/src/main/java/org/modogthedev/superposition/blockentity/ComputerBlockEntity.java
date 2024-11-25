package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class ComputerBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    private Card card;

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.COMPUTER.get(), pos, state);
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
        card = Card.loadNew(tag);
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
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (card != null)
            card.save(pTag);
    }


    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        card = Card.loadNew(pTag);
    }

    @Override
    public void tick() {
        preTick();
        if (level.isClientSide) {
            resetTooltip();
            if (card == null)
                addTooltip(Component.literal("No Card"));
            else {
                addTooltip(Component.literal("Computer Status:"));
                addTooltip(Component.literal("Card - ").append(Component.translatable("item.superposition." + getCard().getSelfReference().getPath())));
            }
        }
        if (card != null) {
            for (Signal signal : getSignals()) {
                card.modulateSignal(signal);
            }
        }
        super.tick();
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        if (card != null) {
            boolean shouldThrow = card.modulateSignal(signal);
            if (shouldThrow)
                return null;
        } else
            return null;
        return super.modulateSignal(signal, updateTooltip);
    }

    @Override
    public BlockPos getDataPos() {
        return getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getOpposite(), 1);
    }
}