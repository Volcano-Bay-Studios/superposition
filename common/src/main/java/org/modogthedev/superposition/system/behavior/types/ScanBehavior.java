package org.modogthedev.superposition.system.behavior.types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;

public interface ScanBehavior {
    /**
     * Retrieves information from a block
     * @param tag
     * @param level
     * @param pos
     * @param state
     */
    public abstract void scan(CompoundTag tag, AnalyserBlockEntity analyserBlockEntity, Level level, BlockPos pos, BlockState state);
}
