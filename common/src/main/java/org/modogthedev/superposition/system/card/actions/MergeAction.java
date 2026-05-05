package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.BiModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class MergeAction extends Action implements BiModifyAction {

    public MergeAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal firstSignal, Signal secondSignal) {
        if (firstSignal != null && firstSignal.getEncodedData() != null && secondSignal != null && secondSignal.getEncodedData() != null) {
            firstSignal.encode(firstSignal.getEncodedData().compoundTagData().merge(secondSignal.getEncodedData().compoundTagData()));
        }
        return firstSignal;
    }
}
