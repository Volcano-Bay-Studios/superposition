package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AmplifierBlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.ScanAction;
import org.modogthedev.superposition.system.signal.Signal;

public class TemperatureCard extends Action implements ScanAction {
    public TemperatureCard(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public void scan(Signal signal, BlockEntity blockEntity) {
        if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
            BlockEntity blockEntity1 = blockEntity.getLevel().getBlockEntity(analyserBlockEntity.getAnalysisPosition());
            if (blockEntity1 instanceof AmplifierBlockEntity amplifierBlockEntity) {
                signal.encode(amplifierBlockEntity.temp);
            }
        }
    }

    @Override
    public ItemStack getThumbnailItem() {
        return Items.LAVA_BUCKET.getDefaultInstance();
    }
}
