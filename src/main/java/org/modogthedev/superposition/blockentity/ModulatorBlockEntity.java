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

    public ModulatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.SIGNAL_GENERATOR.get(), pos, state);
    }



    @Override
    public void tick() {
    }

}