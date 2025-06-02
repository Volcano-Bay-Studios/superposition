package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.ScanAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;

public class ContainerAction extends Action implements ScanAction {

    public ContainerAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public void scan(Signal signal, BlockEntity blockEntity) {
        if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
            BlockEntity blockEntity1 = blockEntity.getLevel().getBlockEntity(analyserBlockEntity.getAnalysisPosition());
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
                signal.encode(tag);
            }
        }
    }

    @Override
    public ItemStack getThumbnailItem() {
        return Items.CHEST.getDefaultInstance();
    }
}
