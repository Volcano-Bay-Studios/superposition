package org.modogthedev.superposition.system.cards.cards;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;

public interface TickingCard {

    void tick(BlockPos computerPos, Level level, ComputerBlockEntity cbe);
}
