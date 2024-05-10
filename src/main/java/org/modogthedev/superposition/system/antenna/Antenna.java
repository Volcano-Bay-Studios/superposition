package org.modogthedev.superposition.system.antenna;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;

public class Antenna {
    public Level level;
    public List<BlockPos> antennaParts = new ArrayList<>();
    public List<Signal> signals = new ArrayList<>();
    public BlockPos antennaActor;
    public boolean reading;
    public Antenna(List<BlockPos> antennaParts, BlockPos antennaActor, Level level) {
        this.antennaParts = antennaParts;
        this.antennaActor = antennaActor;
        this.level = level;
    }

    public boolean isPos(BlockPos pos) {
        return antennaActor.equals(pos);
    }
}
