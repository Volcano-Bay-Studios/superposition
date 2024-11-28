package org.modogthedev.superposition.system.antenna;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.blockentity.AntennaActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlocks;
import org.modogthedev.superposition.system.signal.Signal;
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
    public static List<Antenna> getAntennaList(Level level) {
        ifAbsent(level);
        return antennas.get(level);
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
            postSignalToAntenna(signal, antenna);
        }
    }

    public static void postSignalToAntenna(Signal signal, Antenna antenna) {
        BlockPos pos = Mth.blockPosFromVec3(signal.pos);

        if (!antenna.reading)
            return;
        float bonusFrequency = 0;
        BlockEntity blockEntity = signal.level.getBlockEntity(antenna.antennaActor);
        if (blockEntity instanceof AntennaActorBlockEntity antennaActorBlockEntity) {
            bonusFrequency = antennaActorBlockEntity.getBounusFrequency();
        }

        float dist = (float) Vec3.atLowerCornerOf(antenna.antennaActor).distanceTo(Vec3.atLowerCornerOf(pos));
        float antennaFrequency = Mth.antennaSizeToHz(antenna.antennaParts.size())+bonusFrequency;

        if (dist < signal.maxDist && dist > signal.minDist) {
            Signal signal1 = new Signal(signal);

            Antenna sourceAntenna = AntennaManager.getAntennaActorAntenna(signal.level,signal.sourceAntennaPos);
            signal1.amplitude /= Math.max(1, dist / (1000000000 / signal.frequency));
            signal1.amplitude /= Math.max(1, 1f/(Mth.resonanceAlgorithm(antenna.antennaParts.size(),Math.max(1,signal.sourceAntennaSize))));

            if (signal1.amplitude > 1)
                antenna.signals.add(signal1);
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

    public static Antenna getAntennaActorAntenna(LevelReader levelReader, BlockPos pos) {
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
        if (pos == null)
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
                Antenna ourAntenna = antennas.get(level).get(ordinal);
                ourAntenna.antennaParts = parts;
                ourAntenna.updateDimensions();
                antennas.get(level).set(ordinal, ourAntenna);
                if (blockEntity instanceof AntennaActorBlockEntity antennaActorBlockEntity) {
                    antennaActorBlockEntity.setAntenna(ourAntenna);
                    antennaActorBlockEntity.update();
                }
            } else {
                List<BlockPos> parts = new ArrayList<>(thisPart.parts());
                if (parts.size() < 2)
                    return;
                Antenna newAntenna = new Antenna(parts, thisPart.base(), level);
                newAntenna.reading = (level.getBlockState(thisPart.base()).getBlock().equals(SuperpositionBlocks.RECEIVER.get()));
                newAntenna.updateDimensions();
                antennas.get(level).add(newAntenna);
                BlockEntity blockEntity = level.getBlockEntity(newAntenna.antennaActor);
                if (blockEntity instanceof AntennaActorBlockEntity antennaActorBlockEntity) {
                    antennaActorBlockEntity.update();
                }
            }
        } else {
            int ordinal = get(pos, level);
            if (ordinal >= 0) {
                antennas.get(level).remove(antennas.get(level).get(ordinal));
            }
        }
    }
}
