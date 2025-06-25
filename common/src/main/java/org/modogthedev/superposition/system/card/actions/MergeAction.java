package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.AnyModifyAction;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;

public class MergeAction extends Action implements AnyModifyAction {

    public MergeAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public List<Signal> modify(List<Signal> signals) {
        if (signals.isEmpty()) return null;
        Signal main = null;
        for (Signal s : signals) {
            if (s == null || s.getEncodedData() == null || s.getEncodedData().compoundTagData() == null) continue;
            if (main == null) {
                main = s;
                continue;
            }
            main.encode(s.getEncodedData().compoundTagData().merge(main.getEncodedData().compoundTagData()));
        }
        List<Signal> returnSignals = new ArrayList<>();
        returnSignals.add(main);
        return returnSignals;
    }

    @Override
    public ItemStack getThumbnailItem() {
        return Items.CRAFTER.getDefaultInstance();
    }
}
