package org.modogthedev.superposition.system.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

public interface ManipulateAction {
    void manipulate(Signal signal, Level level, BlockPos pos);
}
