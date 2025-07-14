package org.modogthedev.superposition.system.card.actions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.MonoModifyAction;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SuperpositionMth;

public class AmplitudeAction extends Action implements MonoModifyAction {

    public AmplitudeAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal signal) {
        signal.encode(SuperpositionMth.mapAmplitude(signal.getAmplitude()));
        return signal;
    }
}
