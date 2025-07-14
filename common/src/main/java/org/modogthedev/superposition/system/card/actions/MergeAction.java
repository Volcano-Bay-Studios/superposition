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
    public Signal modify(Signal signal, Signal periphrealSignal) {
        if (signal != null && signal.getEncodedData() != null && periphrealSignal != null && periphrealSignal.getEncodedData() != null) {
            signal.encode(signal.getEncodedData().compoundTagData().merge(periphrealSignal.getEncodedData().compoundTagData()));
        }
        return signal;
    }
}
