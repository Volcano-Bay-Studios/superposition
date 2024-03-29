package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.core.ModBlockEntity;
import org.modogthedev.superposition.particle.ParticleManager;
import org.modogthedev.superposition.util.TickableBlockEntity;

public class SignalGeneratorBlockEntity extends BlockEntity implements TickableBlockEntity {
    Vec3 pos = new Vec3(this.getBlockPos().getX(),this.getBlockPos().getY(),this.getBlockPos().getZ());

    public SignalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.SIGNAL_GENERATOR.get(), pos, state);
    }

    @Override
    public void tick() {
        ParticleManager.addParticle(pos,this.getLevel(),1,1,Vec3.ZERO);
    }

}
