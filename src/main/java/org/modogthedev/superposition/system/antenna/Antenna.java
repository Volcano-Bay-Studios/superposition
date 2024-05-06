package org.modogthedev.superposition.system.antenna;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Antenna {
    public List<BlockPos> antennaParts = new ArrayList<>();
    public BlockPos amplifierBlock;
    public boolean isPos(BlockPos pos) {
        return amplifierBlock.equals(pos);
    }
}
