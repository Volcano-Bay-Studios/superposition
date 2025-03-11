package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.nbt.CompoundTag;
import org.modogthedev.superposition.system.signal.Signal;

public interface ManipulatorCard {
    /**
     * Encodes extra data to be used by Manipulators
     * @param tag The tag that is being sent to the peripheral
     * @param signal The current peripheral signal
     */
    void addOutbound(CompoundTag tag, Signal signal);
}
