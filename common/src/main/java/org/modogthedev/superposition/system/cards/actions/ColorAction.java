package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.ScanAction;
import org.modogthedev.superposition.system.signal.Signal;

public class ColorAction extends Action implements ScanAction {

    public ColorAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public void scan(Signal signal, BlockEntity blockEntity) {
        if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity)
            signal.encode(analyserBlockEntity.getLevel().getBlockState(analyserBlockEntity.getAnalysisPosition()).getBlock().defaultMapColor().col);
    }
}
