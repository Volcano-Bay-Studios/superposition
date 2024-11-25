package org.modogthedev.superposition.system.cards.codecs;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.system.cards.Card;

public class TickingCard extends Card {
    public TickingCard(ResourceLocation card) {
        super(card);
    }
    public BlockPos inputCablePos;
    public BlockPos outputCablePos;
    public BlockPos peripheralCablePos;

    public void tick(BlockPos computerPos, Level level, ComputerBlockEntity cbe) {

    }
}
