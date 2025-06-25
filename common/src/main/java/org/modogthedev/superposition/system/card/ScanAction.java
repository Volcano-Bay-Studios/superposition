package org.modogthedev.superposition.system.card;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.system.signal.Signal;

public interface ScanAction extends PeriphrealAction {

    void scan(Signal signal, BlockEntity blockEntity);
}
