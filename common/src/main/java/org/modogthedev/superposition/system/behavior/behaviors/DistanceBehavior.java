package org.modogthedev.superposition.system.behavior.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.behavior.Behavior;
import org.modogthedev.superposition.system.behavior.types.ScanBehavior;

public class DistanceBehavior extends Behavior implements ScanBehavior {
    public DistanceBehavior(ResourceLocation selfReference) {
        super(selfReference);
    }

    @Override
    public void scan(CompoundTag tag, AnalyserBlockEntity analyserBlockEntity, Level level, BlockPos pos, BlockState state) {
        assert analyserBlockEntity.getLevel() != null;
        tag.putFloat(getSelfReference().getPath(),analyserBlockEntity.distance);
    }
}
