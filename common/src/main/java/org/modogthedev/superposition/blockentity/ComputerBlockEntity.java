package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class ComputerBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    private Card card;

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.AMPLIFIER.get(), pos, state);
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
        level.setBlock(getBlockPos(),getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES,tag.getBoolean("swap")),2);
//        getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap"));
    }
    public static float getRedstoneOffset(Level level, BlockPos pos) {
        return level.getSignal(pos,level.getBlockState(pos).getValue(AmplifierBlock.FACING).getOpposite());
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (card != null)
            card.save(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        card = Card.loadNew(pTag);
    }

    @Override
    public void tick() {
        preTick();
        if (level.isClientSide) {
        }
        super.tick();
    }

    @Override
    public BlockPos getDataPos() {
        return getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getOpposite(), 1);
    }
}