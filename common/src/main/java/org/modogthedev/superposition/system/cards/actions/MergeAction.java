package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.AnyModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

public class MergeAction extends Action implements AnyModifyAction {

    public MergeAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal... signals) {
        if (signals.length == 0) return null;
        Signal main = null;
        for (Signal s : signals) {
            if (s == null || s.getEncodedData() == null || s.getEncodedData().compoundTagData() == null) continue;
            if (main == null) {
                main = s;
                continue;
            }
            main.encode(s.getEncodedData().compoundTagData().merge(main.getEncodedData().compoundTagData()));
        }
        return main;
    }

    @Override
    public ItemStack getThumbnailItem() {
        return Items.CRAFTER.getDefaultInstance();
    }
}
