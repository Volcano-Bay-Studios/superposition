package org.modogthedev.superposition.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.system.antenna.AntennaManager;
import org.modogthedev.superposition.util.BlockHelper;

public class AntennaBlock extends IronBarsBlock {
    public AntennaBlock(Properties p_54198_) {
        super(p_54198_);
    }

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        super.onBlockStateChange(level, pos, oldState, newState);
        AntennaManager.antennaPartUpdate(level,pos);
    }

}
