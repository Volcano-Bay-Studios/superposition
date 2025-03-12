package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.world.RedstoneWorld;
import org.modogthedev.superposition.util.DataHelper;

public class RedstoneCard extends Card implements PeripheralCard, ManipulatorCard {

    public RedstoneCard(ResourceLocation card) {
        super(card);
    }

    public RedstoneCard(Card card) {
        super(card);
    }

    @Override
    public void peripheralEncode(Signal signal, BlockEntity blockEntity) {
        int value = 0;
        if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
            value = blockEntity.getLevel().getBestNeighborSignal(analyserBlockEntity.getAnalysisPosition());
        }
        signal.encode(value);
    }

    @Override
    public void affectBlock(Signal signal, Level level, BlockPos pos) {
        int power = DataHelper.getIntKey(signal,"power");
        if (power > 0) {
            RedstoneWorld.setPower(level,pos, Mth.clamp(power,0,15));
        }
    }

    @Override
    public void addOutbound(CompoundTag tag, Signal signal) {
        int power = DataHelper.getIntValue(signal);
        if ( power > 0) {
            tag.putInt("power",power);
        }
    }

    @Override
    public Card copy() {
        return new RedstoneCard(this);
    }
}
