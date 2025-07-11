package org.modogthedev.superposition.system.behavior.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.behavior.Behavior;
import org.modogthedev.superposition.system.behavior.types.ScanBehavior;

public class ColorBehavior extends Behavior implements ScanBehavior {
    public ColorBehavior(ResourceLocation selfReference) {
        super(selfReference);
    }

    @Override
    public void scan(CompoundTag tag, AnalyserBlockEntity analyserBlockEntity, Level level, BlockPos pos, BlockState state) {
        tag.putInt(getSelfReference().getPath(),state.getBlock().defaultMapColor().col);
    }
}
