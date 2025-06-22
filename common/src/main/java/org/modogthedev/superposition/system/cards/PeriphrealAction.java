package org.modogthedev.superposition.system.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public interface PeriphrealAction extends ExecutableAction {
    @Override
    default List<Signal> execute(List<Signal> signals, Level level, BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", getLocation().toString());
        for (Signal signal : signals) {
            signal.encode(tag.copy());
        }
        return signals;
    }

    @Override
    default int getParameterCount() {
        return 0;
    }

    ResourceLocation getLocation();
}
