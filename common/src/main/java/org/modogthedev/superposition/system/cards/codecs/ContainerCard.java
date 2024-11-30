package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.core.Direction;
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
        if (peripherialPosition != null) {
            BlockEntity blockEntity = computerBlockEntity.getLevel().getBlockEntity(peripherialPosition);
            if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
                BlockEntity blockEntity1 = computerBlockEntity.getLevel().getBlockEntity(analyserBlockEntity.getAnalysisPosition());
                if (blockEntity1 instanceof WorldlyContainer worldlyContainer) {
                    List<ItemStack> stacks = getItems(worldlyContainer,analyserBlockEntity.getFacing().getOpposite());
                }
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
        int[] is = getSlots(container, direction);
        if (is != null) {
            int[] var3 = is;
            int var4 = is.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                int i = var3[var5];
                ItemStack itemStack = container.getItem(i);
                stacks.add(itemStack);
            }
        }
        return stacks;
    }
    @Override
    public Card copy() {
        return new ContainerCard(this);
    }
}
