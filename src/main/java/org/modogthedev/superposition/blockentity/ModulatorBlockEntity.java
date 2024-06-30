package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.block.ModulatorBlock;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorBlockEntity;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;

public class ModulatorBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    Vec3 pos = new Vec3(this.getBlockPos().getX(),this.getBlockPos().getY(),this.getBlockPos().getZ());
    public float modRate;
    public float redstoneMod;

    public ModulatorBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntity.MODULATOR.get(), pos, state);
    }
    @Override
    public void writeData(CompoundTag tag) {
        super.writeData(tag);
        this.modRate = tag.getFloat("modRate");
        this.redstoneMod = tag.getFloat("redstoneMod");

        level.setBlock(getBlockPos(),getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES,tag.getBoolean("swap")),2);
//        getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap"));
    }
    public static float getRedstoneOffset(Level level, BlockPos pos) {
        return level.getSignal(pos,level.getBlockState(pos).getValue(ModulatorBlock.FACING).getOpposite());
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putFloat("modRate", modRate);
        pTag.putFloat("redstoneMod",redstoneMod);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.modRate = pTag.getFloat("modRate");
        this.redstoneMod = pTag.getFloat("redstoneMod");
    }

    @Override
    public Signal modulateSignal(Signal signal) {
        if (signal != null)
            signal.amplitude += (modRate+(getRedstoneOffset(level, getBlockPos())*((float) redstoneMod /15)));
        return signal;
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public BlockPos getDataPos() {
        return getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getOpposite(), 1);
    }
}