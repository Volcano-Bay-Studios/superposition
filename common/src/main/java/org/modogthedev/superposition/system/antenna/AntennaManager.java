package org.modogthedev.superposition.system.antenna;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.blockentity.AntennaActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlocks;
import org.modogthedev.superposition.system.antenna.type.PhysicalAntenna;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.BlockHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AntennaManager {
    public static HashMap<Level, List<Antenna>> antennas = new HashMap<>();

    public static void tickAntennas(ServerLevel level) {
        clearSignals(level);
        List<Antenna> levelAntennas = antennas.get(level);
        if (levelAntennas != null) {
            for (Antenna antenna : levelAntennas) {
                antenna.signals.clear();
                SignalManager.postSignalsToAntenna(antenna);
            }
        }
    }

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
        List<Antenna> levelAntennas = antennas.get(level);
        if (levelAntennas == null) {
            return;
        }
        for (Antenna antenna : levelAntennas) {
            antenna.signals.clear();
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
        if (pos == null) {
            return;
        }
        if (reader.getBlockEntity(pos.below()) instanceof AntennaActorBlockEntity) {
            pos = pos.below();
        }
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
                if (ourAntenna instanceof PhysicalAntenna physicalAntenna) {
                    physicalAntenna.antennaParts = parts;
                    physicalAntenna.updateDimensions();
                    antennas.get(level).set(ordinal, ourAntenna);
                    if (blockEntity instanceof AntennaActorBlockEntity antennaActorBlockEntity) {
                        antennaActorBlockEntity.setAntenna(ourAntenna);
                        antennaActorBlockEntity.update();
                    }
                }
            } else {
                List<BlockPos> parts = new ArrayList<>(thisPart.parts());
                if (parts.size() < 2) {
                    return;
                }
                PhysicalAntenna newAntenna = new PhysicalAntenna(parts, thisPart.base(), level);
                newAntenna.isReceiving = (level.getBlockState(thisPart.base()).getBlock().equals(SuperpositionBlocks.RECEIVER.get()));
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

    public static void clear() {
        antennas.clear();
    }
}
