package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.blockentity.util.CardHolder;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.system.card.Card;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalHelper;
import org.modogthedev.superposition.util.SuperpositionMth;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.List;

public class InscriberBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity, CardHolder {
    private Card card = null;
    private Signal outputSignal = null;

    public InscriberBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.INSCRIBER.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        saveCard(tag);
        super.saveAdditional(tag, registries);
    }

    public CompoundTag saveCard(CompoundTag tag) {
        if (card != null) {
            tag.put("card", card.save(new CompoundTag()));
        }
        return tag;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("card")) {
            this.card = new Card(tag.getCompound("card"));
        }
        super.loadAdditional(tag, registries);
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        if (tag.contains("card")) {
            this.card = new Card(tag.getCompound("card"));
        }
        super.loadSyncedData(tag);
    }

    @Override
    public void setupConfigTooltips() {
        super.setupConfigTooltips();
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Override
    public boolean specialAddSignals(List<Signal> signals, Direction face) {
        for (Signal signal : signals) {
            if (signal != null && signal.getEncodedData() != null) {
                card.load(signal.getEncodedData().compoundTagData());
            }
        }
        return true;
    }

    @Override
    public List<Signal> getSignals() {
        if (outputSignal == null) {
            outputSignal = new Signal(SuperpositionMth.convertVec(getBlockPos()), level, SuperpositionConstants.periphrealFrequency, 1, SuperpositionConstants.periphrealFrequency / 100000);
        }
        if (card != null) {
            outputSignal.encode(card.save(new CompoundTag()));
        }
        return SignalHelper.listOf(outputSignal);
    }

    @Override
    public void tick() {
        if (outputSignal == null) {
            outputSignal = new Signal(SuperpositionMth.convertVec(getBlockPos()), level, SuperpositionConstants.periphrealFrequency, 1, SuperpositionConstants.periphrealFrequency / 100000);
        }
        if (level.isClientSide) {
            resetTooltip();
            addTooltip(Component.literal("Inscriber Status:"));
            if (card != null) {
                addTooltip(Component.literal("Editing '" + card.title + "'"));
            } else {
                addTooltip(Component.literal("No Information"));
            }
        }
        super.tick();
    }
}


