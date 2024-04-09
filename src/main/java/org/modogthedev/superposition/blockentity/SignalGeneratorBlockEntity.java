package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.ModBlockEntity;
import org.modogthedev.superposition.particle.ParticleManager;
import org.modogthedev.superposition.util.SyncedBlockEntity;
import org.modogthedev.superposition.util.TickableBlockEntity;

public class SignalGeneratorBlockEntity extends SyncedBlockEntity implements TickableBlockEntity {
    Vec3 pos = new Vec3(this.getBlockPos().getX(),this.getBlockPos().getY(),this.getBlockPos().getZ());
    public float frequency;


    public SignalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.SIGNAL_GENERATOR.get(), pos, state);
    }
    @Override
    public void writeData(CompoundTag tag) {
        super.writeData(tag);
        this.frequency = tag.getFloat("frequency");
        boolean animated = frequency > .7f;

        level.setBlock(getBlockPos(),getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES,tag.getBoolean("swap")).setValue(SignalGeneratorBlock.ON, animated),2);
//        getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putFloat("frequency", frequency);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.frequency = pTag.getFloat("frequency");
    }

    @Override
    public void tick() {
        int frequency = this.getBlockState().getValue(SignalGeneratorBlock.BASE_FREQUENCY);
    }
    
}
