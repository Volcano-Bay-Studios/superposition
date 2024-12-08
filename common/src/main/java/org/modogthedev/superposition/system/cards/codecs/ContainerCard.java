package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;

public class ContainerCard extends Card {
    public ContainerCard(ResourceLocation card) {
        super(card);
    }

    public ContainerCard(Card card) {
        super(card);
    }

    @Override
    public void modulateSignal(Signal signal) {
        if (periphrealBlockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
            BlockEntity blockEntity1 = periphrealBlockEntity.getLevel().getBlockEntity(analyserBlockEntity.getAnalysisPosition());
            if (blockEntity1 instanceof WorldlyContainer worldlyContainer) {
                List<ItemStack> stacks = getItems(worldlyContainer, analyserBlockEntity.getFacing().getOpposite());
                CompoundTag tag = new CompoundTag();
                for (ItemStack stack : stacks) {
                    tag.putInt(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(),stack.getCount());
                }
                signal.encode(tag.getAsString());
            }
        }
    }

    private static int[] getSlots(Container container, Direction direction) {
        if (container instanceof WorldlyContainer worldlyContainer) {
            return worldlyContainer.getSlotsForFace(direction);
        }
        return null;
    }

    private static List<ItemStack> getItems(Container container, Direction direction) {
        List<ItemStack> stacks = new ArrayList<>();
        int[] slots = getSlots(container, direction);
        if (slots != null) {
            for (int slot : slots) {
                stacks.add(container.getItem(slot));
            }
        }
        return stacks;
    }
    @Override
    public boolean requiresPeriphreal() {
        return true;
    }
    @Override
    public Card copy() {
        return new ContainerCard(this);
    }
}
