package org.modogthedev.superposition.system.antenna;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlocks;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.AntennaActorBlockEntity;
import org.modogthedev.superposition.util.BlockHelper;
import org.modogthedev.superposition.util.Mth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AntennaManager {
    public static HashMap<Level, List<Antenna>> antennas = new HashMap<>();

    //    public static List<Antenna> antennas = new ArrayList<>();
    private static void ifAbsent(Level level) {
        if (!antennas.containsKey(level)) {
            antennas.put(level, new ArrayList<>());
        }
    }

    public static void clearSignals(Level level) {
        if (antennas.get(level) == null)
            return;
        for (Antenna antenna : antennas.get(level)) {
            antenna.signals.clear();
        }
    }

    public static void postSignal(Signal signal) {
        Level level = signal.level;
        BlockPos pos = Mth.blockPosFromVec3(signal.pos);
        for (Antenna antenna : antennas.get(level)) {

            if (!antenna.reading)
                continue;
            float dist = (float) antenna.antennaActor.distSqr(pos);
            if (dist < signal.maxDist && dist > signal.minDist) {
                antenna.signals.add(signal);
            }
        }
    }

    public static void postSignalToAntenna(Signal signal, Antenna antenna) {
        Level level = signal.level;
        BlockPos pos = Mth.blockPosFromVec3(signal.pos);
        if (!antenna.reading)
            return;
        float dist = (float) antenna.antennaActor.distSqr(pos);
        if (dist < signal.maxDist && dist > signal.minDist) {
            antenna.signals.add(signal);
        }
    }

    public static int get(BlockPos pos, Level level) {
        ifAbsent(level);
        int i = 0;
        for (Antenna antenna : antennas.get(level)) {
            if (antenna.isPos(pos)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static Antenna getAmplifierAntenna(LevelReader levelReader, BlockPos pos) {
        Level level = (Level) levelReader;
        Antenna antenna = null;
        int ordinal = get(pos, level);
        if (ordinal >= 0) {
            antenna = antennas.get(level).get(ordinal);
        } else {
            antennaPartUpdate(levelReader, pos);
            ordinal = get(pos, level);
            if (ordinal >= 0) {
                antenna = antennas.get(level).get(ordinal);
            }
        }
        return antenna;
    }

    public static void antennaPartUpdate(LevelReader reader, BlockPos pos) {
        Level level = (Level) reader;
        if (level.isClientSide)
            return;
        if (reader.getBlockEntity(pos.below()) instanceof AntennaActorBlockEntity)
            pos = pos.below();
        BlockHelper.AntennaPart thisPart = BlockHelper.getAntennaPart(reader, pos);
        if (thisPart.base() != null) {
            int ordinal = get(thisPart.base(), level);
            if (ordinal >= 0) {
                thisPart = BlockHelper.getAntennaPart(reader, thisPart.base());
                BlockEntity blockEntity = level.getBlockEntity(antennas.get(level).get(ordinal).antennaActor);
                List<BlockPos> parts = new ArrayList<>(thisPart.parts());

                if (parts.size() < 2) {
                    antennas.get(level).remove(antennas.get(level).get(ordinal));
                    if (blockEntity instanceof AntennaActorBlockEntity antennaActorBlockEntity) {
                        antennaActorBlockEntity.removeAntenna();
                    }
                    return;
                }
                antennas.get(level).set(ordinal, antennas.get(level).get(ordinal));
                if (blockEntity instanceof AntennaActorBlockEntity antennaActorBlockEntity) {
                    antennaActorBlockEntity.update();
                }
            } else {
                List<BlockPos> parts = new ArrayList<>(thisPart.parts());
                if (parts.size() < 2)
                    return;
                Antenna newAntenna = new Antenna(parts, thisPart.base(), level);
                newAntenna.reading = (level.getBlockState(thisPart.base()).getBlock().equals(SuperpositionBlocks.RECEIVER.get()));
                antennas.get(level).add(newAntenna);
                BlockEntity blockEntity = level.getBlockEntity(newAntenna.antennaActor);
                if (blockEntity instanceof AntennaActorBlockEntity antennaActorBlockEntity) {
                    antennaActorBlockEntity.update();
                }
            }
        }
    }
}
