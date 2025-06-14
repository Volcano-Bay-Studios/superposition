package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.MonoModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class FrequencyCard extends Action implements MonoModifyAction {

    public FrequencyCard(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal signal) {
        signal.encode(signal.getFrequency());
        return signal;
    }

    @Override
    public ItemStack getThumbnailItem() {
        return Items.REPEATER.getDefaultInstance();
    }
}