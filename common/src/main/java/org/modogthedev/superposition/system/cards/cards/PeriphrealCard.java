package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.system.signal.Signal;

public interface PeriphrealCard {

    void returnSignal(Signal signal, BlockEntity blockEntity);
}
