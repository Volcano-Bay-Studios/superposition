package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.ModBlockEntity;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaManager;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorBlockEntity;
import org.modogthedev.superposition.util.TickableBlockEntity;

public class AmplifierBlockEntity  extends SignalActorBlockEntity implements TickableBlockEntity {
    public Antenna antenna;
    public AmplifierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.AMPLIFIER.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos sidedPos = getSwappedPos();
        level.addParticle(ParticleTypes.FLAME, sidedPos.getX(),sidedPos.getY(),sidedPos.getZ(), 0, 0, 0);
        int power = level.getSignal(worldPosition,getSwappedSide());
        if (antenna == null) {
            Antenna getAntenna = AntennaManager.getAmplifierAntenna(level,worldPosition);
            if (getAntenna != null)
                antenna = getAntenna;
        }
        if (power > 0) {
            Signal signalForBroadcast = createSignal(new Object());
        }
    }
}
