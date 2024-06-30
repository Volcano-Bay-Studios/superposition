package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.AntennaActorBlockEntity;
import org.modogthedev.superposition.util.SignalActorBlockEntity;

public class AmplifierBlockEntity  extends AntennaActorBlockEntity {
    Signal signal;
    public AmplifierBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntity.AMPLIFIER.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide)
            return;
//        System.out.println(SignalManager.transmittedSignals.get(level).size());
        BlockPos sidedPos = getSwappedPos();
        int power = level.getSignal(worldPosition,getSwappedSide());

        if (antenna != null && power > 0) {
            SignalActorBlockEntity signalActorBlockEntity = topBE(new Object());
            if (signalActorBlockEntity != null) {
                Signal signalForBroadcast = signalActorBlockEntity.createSignal(new Object());
                if (signalForBroadcast != null) {
                    signalForBroadcast.pos = new Vec3(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ());
                    signalForBroadcast.emitting = true;
                    signalForBroadcast.level = level;
                    SignalManager.addSignal(signalForBroadcast);
                    signal = signalForBroadcast;
                }
            }
        } else if (signal != null) {
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
