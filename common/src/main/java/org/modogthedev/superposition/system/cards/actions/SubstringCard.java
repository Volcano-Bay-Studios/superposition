package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.BiModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class SubstringCard extends Action implements BiModifyAction {

    public SubstringCard(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal signal, Signal periphrealSignal) {
        if (periphrealSignal != null && signal.getEncodedData() != null && periphrealSignal.getEncodedData() != null) {
            String s = signal.getEncodedData().stringValue();
            int cutPosition = periphrealSignal.getEncodedData().intValue();
            if (s != null) {
                if (cutPosition >= 0) {
                    signal.encode(s.substring(0, Math.max(0, s.length() - cutPosition)));
                } else {
                    signal.encode(s.substring(-cutPosition));
                }
            }
        }
        return signal;
    }

    @Override
    public ItemStack getThumbnailItem() {
        return Items.SHEARS.getDefaultInstance();
    }
}
