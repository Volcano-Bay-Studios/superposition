package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.ModBlockEntity;
import org.modogthedev.superposition.util.SyncedBlockEntity;
import org.modogthedev.superposition.util.TickableBlockEntity;

public class ModulatorBlockEntity extends SyncedBlockEntity implements TickableBlockEntity {
    Vec3 pos = new Vec3(this.getBlockPos().getX(),this.getBlockPos().getY(),this.getBlockPos().getZ());
    public float modRate;

    public ModulatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.MODULATOR.get(), pos, state);
    }
    @Override
    public void writeData(CompoundTag tag) {
        super.writeData(tag);
        this.modRate = tag.getFloat("modRate");

        level.setBlock(getBlockPos(),getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES,tag.getBoolean("swap")),2);
//        getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putFloat("modRate", modRate);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.modRate = pTag.getFloat("modRate");
    }



    @Override
    public void tick() {
    }

}