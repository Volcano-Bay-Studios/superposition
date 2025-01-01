package org.modogthedev.superposition.system.cable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.*;

public class CablePassthroughManager {
    private static Map<ResourceKey<Level>, Map<BlockPos, List<Signal>>> currentHeldSignals = new HashMap<>();
    private static Map<ResourceKey<Level>, Map<BlockPos, List<Signal>>> clientCurrentHeldSignals = new HashMap<>();

    private static Map<ResourceKey<Level>, Map<BlockPos, List<Signal>>> newHeldSignals = new HashMap<>();
    private static Map<ResourceKey<Level>, Map<BlockPos, List<Signal>>> clientNewHeldSignals = new HashMap<>();

    private static Map<ResourceKey<Level>, Map<BlockPos, List<Signal>>> getCurrentHeldSignals(Level level) {
        return level.isClientSide ? clientCurrentHeldSignals : currentHeldSignals;
    }

    private static Map<ResourceKey<Level>, Map<BlockPos, List<Signal>>> getNewHeldSignals(Level level) {
        return level.isClientSide ? clientNewHeldSignals : newHeldSignals;
    }

    public static void tick(Level level) {
        // Update currentHeldSignals
        accessedLevel(level);
        Map<BlockPos, List<Signal>> map = getCurrentHeldSignals(level).get(level.dimension());
        Map<BlockPos, List<Signal>> oldMap = getNewHeldSignals(level).get(level.dimension());
        List<BlockPos> removedPos = new ArrayList<>();
        for (BlockPos pos : map.keySet()) {
            if (!oldMap.containsKey(pos)) {
                removedPos.add(pos);
            }
        }
        for (BlockPos pos : oldMap.keySet()) {
            if (map.containsKey(pos)) {
                updateList(map.get(pos), oldMap.get(pos));
            } else {
                map.put(pos,oldMap.get(pos));
            }
        }
        for (BlockPos pos : removedPos) {
            map.remove(pos);
        }
        // Wipe newHeldSignals
        getNewHeldSignals(level).clear();
    }

    public static void addSignalsToBlock(Level level, BlockPos pos, List<Signal> signals) {
        accessedLevel(level);
        if (getNewHeldSignals(level).get(level.dimension()).containsKey(pos)) {
            getNewHeldSignals(level).get(level.dimension()).get(pos).addAll(signals);
        } else {
            getNewHeldSignals(level).get(level.dimension()).put(pos, signals);
        }
    }

    public static List<Signal> getSignalsFromBlock(Level level, BlockPos pos) {
        accessedLevel(level);
        if (getCurrentHeldSignals(level).get(level.dimension()).containsKey(pos)) {
            return getCurrentHeldSignals(level).get(level.dimension()).get(pos);
        }
        return null;
    }

    public static void accessedLevel(Level level) {
        if (!getCurrentHeldSignals(level).containsKey(level.dimension())) {
            getCurrentHeldSignals(level).put(level.dimension(), new HashMap<>());
        }
        if (!getNewHeldSignals(level).containsKey(level.dimension())) {
            getNewHeldSignals(level).put(level.dimension(), new HashMap<>());
        }
    }

    public static void updateList(List<Signal> oldSignals, List<Signal> newSignals) {
        if (oldSignals.size() == newSignals.size()) {
            for (int i = 0; i < newSignals.size(); i++) {
                oldSignals.get(i).copy(newSignals.get(i));
            }
        } else if (oldSignals.size() > newSignals.size()) {
            ListIterator<Signal> iterator = oldSignals.listIterator();
            while (iterator.hasNext()) {
                int i = iterator.nextIndex();
                Signal signal = iterator.next();
                if (i >= newSignals.size()) {
                    iterator.remove();
                    continue;
                }
                signal.copy(newSignals.get(i));
            }
        } else {
            for (int i = 0; i < newSignals.size(); i++) {
                Signal signal = newSignals.get(i);
                if (i >= oldSignals.size()) {
                    oldSignals.add(new Signal(signal));
                    continue;
                }
                oldSignals.get(i).copy(signal);
            }
        }
    }
}
