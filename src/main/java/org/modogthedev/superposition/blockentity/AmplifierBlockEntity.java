package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.AntennaActorBlockEntity;
import org.modogthedev.superposition.util.SignalActorBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class AmplifierBlockEntity  extends AntennaActorBlockEntity {
    Signal signal;
    public AmplifierBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntity.AMPLIFIER.get(), pos, state);
    }

    @Override
    public void tick() {
        List<Component> tooltip = new ArrayList<>();
        this.setTooltip(tooltip);
        super.tick();
//        System.out.println(SignalManager.transmittedSignals.get(level).size());
        BlockPos sidedPos = getSwappedPos();
        int power = level.getSignal(worldPosition,getSwappedSide());

        if (antenna != null && power > 0) {
            Signal signalForBroadcast = createSignal(new Object());
            if (signalForBroadcast != null) {
                signalForBroadcast.pos = new Vec3(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ());
                signalForBroadcast.emitting = true;
                signalForBroadcast.level = level;
                SignalManager.addSignal(signalForBroadcast);
                signal = signalForBroadcast;
            }
        } else if (signal != null) {
            stopTransmission();
        }
    }
    public void stopTransmission() {
        endSignal(new Object());
        SignalManager.stopSignal(signal);
        signal = null;
    }
}
