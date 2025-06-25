package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.MonoModifyAction;
import org.modogthedev.superposition.system.card.SynchronizedCard;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;

public class SlaveCard extends Action implements SynchronizedCard, MonoModifyAction {

    public SlaveCard(ResourceLocation action, Information info) {
        super(action, info);
    }

    public EncodedData<?> encodedData;

    @Override
    public Signal modify(Signal signal) {
        if (signal != null && encodedData != null) {
            signal.setEncodedData(encodedData);
        }
        return signal;
    }

    public EncodedData<?> getEncodedData() {
        return encodedData;
    }

    public void setEncodedData(EncodedData<?> encodedData) {
        this.encodedData = encodedData;
    }

    @Override
    public ItemStack getThumbnailItem() {
        return Items.LEAD.getDefaultInstance();
    }
}
