package org.modogthedev.superposition.system.behavior.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.mixin.behavior.FurnaceBehaviorAccessor;
import org.modogthedev.superposition.system.behavior.Behavior;
import org.modogthedev.superposition.system.behavior.types.ScanBehavior;

public class FurnaceBehavior extends Behavior implements ScanBehavior {
    public FurnaceBehavior(ResourceLocation selfReference) {
        super(selfReference);
    }

    @Override
    public void scan(CompoundTag outputTag, AnalyserBlockEntity analyserBlockEntity, Level level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity1 = level.getBlockEntity(pos);
        if (blockEntity1 instanceof AbstractFurnaceBlockEntity furnaceBlockEntity) {
            FurnaceBehaviorAccessor furnace = (FurnaceBehaviorAccessor) furnaceBlockEntity;
            CompoundTag tag = new CompoundTag();
            tag.putInt("litTime",furnace.litTime());
            tag.putInt("litDuration",furnace.litDuration());
            tag.putInt("cookingProgress",furnace.cookingProgress());

            outputTag.put(getPath(),tag);
        }
    }
}
