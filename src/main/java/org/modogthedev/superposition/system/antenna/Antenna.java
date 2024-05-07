package org.modogthedev.superposition.system.antenna;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class Antenna {
    public Level level;
    public List<BlockPos> antennaParts = new ArrayList<>();
    public BlockPos amplifierBlock;
    public Antenna(List<BlockPos> antennaParts, BlockPos amplifierBlock, Level level) {
        this.antennaParts = antennaParts;
        this.amplifierBlock = amplifierBlock;
        this.level = level;
    }

    public boolean isPos(BlockPos pos) {
        return amplifierBlock.equals(pos);
    }
}
