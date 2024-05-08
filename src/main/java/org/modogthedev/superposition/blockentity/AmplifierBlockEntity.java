package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.core.ModBlockEntity;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaManager;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.AntennaActorBlockEntity;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.SignalActorBlockEntity;
import org.modogthedev.superposition.util.TickableBlockEntity;

public class AmplifierBlockEntity  extends AntennaActorBlockEntity {
    Signal signal;
    public AmplifierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.AMPLIFIER.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide)
            return;
//        System.out.println(SignalManager.transmittedSignals.get(level).size());
        BlockPos sidedPos = getSwappedPos();
        int power = level.getSignal(worldPosition,getSwappedSide());

        float amplitude = power;
        if (antenna != null && power > 0) {
            SignalActorBlockEntity signalActorBlockEntity = topBE(new Object());
            if (signalActorBlockEntity != null) {
                Signal signalForBroadcast = signalActorBlockEntity.createSignal(new Object());
                if (signalForBroadcast != null) {
                    signalForBroadcast.pos = new Vec3(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ());
                    signalForBroadcast.amplitude = amplitude;
                    signalForBroadcast.emitting = true;
                    signalForBroadcast.level = level;
                    SignalManager.addSignal(signalForBroadcast);
                    signal = signalForBroadcast;
                }
            }
        } else if (signal != null){
            stopTransmission();
        }
    }
    public void stopTransmission() {
        SignalActorBlockEntity signalActorBlockEntity = topBE(new Object());
        if (signalActorBlockEntity instanceof SignalGeneratorBlockEntity signalGeneratorBlockEntity)
            signalGeneratorBlockEntity.endSignal();
        else
            SignalManager.stopSignal(signal);
        signal = null;
    }
}
