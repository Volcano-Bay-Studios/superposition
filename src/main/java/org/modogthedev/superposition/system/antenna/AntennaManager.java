package org.modogthedev.superposition.system.antenna;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.util.BlockHelper;

import java.util.ArrayList;
import java.util.List;

public class AntennaManager {
    public static List<Antenna> antennas = new ArrayList<>();

    public static Antenna getAntenna(BlockPos basePos) {
        return new Antenna();
    }

    public static void antennaPartUpdate(LevelReader reader, BlockPos pos){
        BlockHelper.AntennaPart thisPart = BlockHelper.getAntennaPart(reader, pos);
        if (thisPart.base() != null) {
            Superposition.LOGGER.info("This is a base!");
        }
    }
}
