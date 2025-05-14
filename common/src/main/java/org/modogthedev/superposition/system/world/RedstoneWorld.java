package org.modogthedev.superposition.system.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RedstoneWorld { // Forgive me for this class is a sin. There is probably a better way to do this.
    private static final HashMap<Level, HashMap<BlockPos, Integer>> oldMap = new HashMap<>();
    private static final HashMap<Level, HashMap<BlockPos, Integer>> redstoneMap = new HashMap<>();
    private static final HashMap<Level, HashMap<BlockPos, Integer>> clientRedstoneMap = new HashMap<>();

    private static HashMap<BlockPos, Integer> getMap(Level level) {
        return level.isClientSide ? clientRedstoneMap.computeIfAbsent(level, (key) -> new HashMap<>()) : redstoneMap.computeIfAbsent(level, (key) -> new HashMap<>());
    }

    public static void tick(Level level) {
        HashMap<BlockPos, Integer> redstone = redstoneMap.computeIfAbsent(level, (key) -> new HashMap<>());
        HashMap<BlockPos, Integer> old = oldMap.computeIfAbsent(level, (key) -> new HashMap<>());

        for (BlockPos pos : redstone.keySet()) {
            if (old.containsKey(pos)) {
                if (!Objects.equals(redstone.get(pos), old.get(pos))) {
                    level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
                    level.updateNeighborsAt(pos.above(), level.getBlockState(pos.above()).getBlock());
                }
            } else {
                level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
                level.updateNeighborsAt(pos.above(), level.getBlockState(pos.above()).getBlock());
            }
            old.remove(pos);
        }
        for (BlockPos pos : old.keySet()) {
            level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
            level.updateNeighborsAt(pos.above(), level.getBlockState(pos.above()).getBlock());
        }

        old.clear();
        old.putAll(redstone);
        redstone.clear();
    }

    public static void clientTick(Level level) {
        clientRedstoneMap.computeIfAbsent(level, (key) -> new HashMap<>()).clear();
    }

    public static void setPower(Level level, BlockPos pos, int power) {
        getMap(level).put(pos, power);
    }

    public static int getPower(Level level, BlockPos pos) {
        Integer i = getValueIfKeyEquals(getMap(level),(pos));
        if (i != null) {
            return i;
        }
        return 0;
    }

    public static int getOldPower(Level level, BlockPos pos) {
        if (level.isClientSide) { return 0; }
        Integer i = getValueIfKeyEquals(oldMap.computeIfAbsent(level, (key) -> new HashMap<>()),(pos));
        if (i != null) {
            return i;
        }
        return 0;
    }


    public static <K, V> V getValueIfKeyEquals(Map<K, V> map, K key) {
        for (K mapKey : map.keySet()) {
            if (Objects.equals(mapKey, key)) {
                return map.get(mapKey);
            }
        }
        return null; // Key not found
    }

    public static void clear() {
        clientRedstoneMap.clear();
    }

    public static class RedstonePos {
        private int x;
        private int y;
        private int z;

        public RedstonePos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public RedstonePos(BlockPos pos) {
            this(pos.getX(), pos.getY(), pos.getZ());
        }

        public BlockPos getBlockPos() {
            return new BlockPos(x, y, z);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RedstonePos redstonePos) {
                return redstonePos.x == x && redstonePos.y == y && redstonePos.z == z;
            }
            return false;
        }
    }
}
