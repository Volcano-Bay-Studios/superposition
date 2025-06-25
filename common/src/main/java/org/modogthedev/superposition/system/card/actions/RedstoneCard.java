package org.modogthedev.superposition.system.card.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.ManipulateAction;
import org.modogthedev.superposition.system.card.ScanAction;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.world.RedstoneWorld;
import org.modogthedev.superposition.util.DataHelper;

public class RedstoneCard extends Action implements ScanAction, ManipulateAction {

    public RedstoneCard(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public void scan(Signal signal, BlockEntity blockEntity) {
        int value = 0;
        if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
            value = blockEntity.getLevel().getBestNeighborSignal(analyserBlockEntity.getAnalysisPosition());
        }
        signal.encode(value);
    }

    @Override
    public void manipulate(Signal signal, Level level, BlockPos pos) {
        int power = DataHelper.getIntKey(signal, "power");
        if (power > 0) {
            RedstoneWorld.setPower(level, pos, Mth.clamp(power, 0, 15));
        }
    }

    @Override
    public void addOutbound(CompoundTag tag, Signal signal) {
        int power = DataHelper.getIntValue(signal);
        if (power > 0) {
            tag.putInt("power", power);
        }
    }

    @Override
    public ItemStack getThumbnailItem() {
        return Items.REDSTONE.getDefaultInstance();
    }

}
