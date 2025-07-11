package org.modogthedev.superposition.system.behavior.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

public interface ManipulateBehavior {
    void manipulate(Signal signal, Level level, BlockPos pos);
}
