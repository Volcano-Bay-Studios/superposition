package org.modogthedev.superposition.system.behavior.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.behavior.Behavior;
import org.modogthedev.superposition.system.behavior.types.ScanBehavior;

import java.util.ArrayList;
import java.util.List;

public class ContainerBehavior extends Behavior implements ScanBehavior {
    public ContainerBehavior(ResourceLocation selfReference) {
        super(selfReference);
    }

    @Override
    public void scan(CompoundTag outputTag, AnalyserBlockEntity analyserBlockEntity, Level level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity1 = level.getBlockEntity(pos);
        if (blockEntity1 instanceof BaseContainerBlockEntity container) {
            List<ItemStack> stacks = new ArrayList<>();
            CompoundTag tag = new CompoundTag();
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (!stack.is(Items.AIR)) {
                    String key = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                    if (!tag.contains(key)) {
                        tag.putInt(key, stack.getCount());
                    } else {
                        tag.putInt(key, stack.getCount() + tag.getInt(key));
                    }
                }
            }
            outputTag.put(getSelfReference().getPath(),tag);
        }
    }
}
