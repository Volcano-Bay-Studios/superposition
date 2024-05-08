package org.modogthedev.superposition.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.block.AntennaBlock;
import org.modogthedev.superposition.block.ReceiverBlock;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BlockHelper {
    //These are all the sides of the block
    private static final Direction[] faces = {
            Direction.DOWN,
            Direction.UP,
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    };

    private static BlockPos getConnectedblocks(LevelReader reader, BlockPos pos, Set<BlockPos> results, List<BlockPos> todo, BlockPos base) {
        BlockPos foundBase = null;

        //Loop through all block faces (All 6 sides around the block)
        for (Direction face : faces) {
            BlockPos relative = pos.relative(face);
            Block b = reader.getBlockState(relative).getBlock();
            //Check if they're both of the same type
            if (b instanceof AntennaBlock) {
                //Add the block if it wasn't added already
                if (results.add(relative)) {

                    //Add this block to the list of blocks that are yet to be done.
                    todo.add(relative);
                }
            } else if (b instanceof AmplifierBlock || b instanceof ReceiverBlock) {
                foundBase = relative;
            }
        }
        return foundBase;
    }
    public static AntennaPart getAntennaPart(LevelReader level, BlockPos pos) {
        Set<BlockPos> set = new HashSet<>();
        LinkedList<BlockPos> list = new LinkedList<>();
        BlockPos basePos = null;

        //Add the current block to the list of blocks that are yet to be done
        list.add(pos);

        //Execute this method for each block in the 'todo' list
        while((pos = list.poll()) != null) {
            BlockPos foundPos = getConnectedblocks(level, pos, set, list, basePos);
            if (foundPos != null) {
                basePos = foundPos;
            }
        }
        return new AntennaPart(set,basePos);
    }
    public record AntennaPart(Set<BlockPos> parts, BlockPos base) {
    }
}
