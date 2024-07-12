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
        preTick();
        List<Component> tooltip = new ArrayList<>();
//        System.out.println(SignalManager.transmittedSignals.get(level).size());
        BlockPos sidedPos = getSwappedPos();
        int power = level.getSignal(worldPosition,getSwappedSide());
        tooltip.add(Component.literal("Amplifier Status:"));
        if (antenna != null)
            tooltip.add(Component.literal("Antenna Classification - "+classifyAntenna()));
        boolean noSignal = false;
        if (antenna != null) {
            Signal signalForBroadcast = createSignal(new Object());
            if (signalForBroadcast != null) {
                if (power > 0) {
                    signalForBroadcast.pos = new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
                    signalForBroadcast.emitting = true;
                    signalForBroadcast.level = level;
                    SignalManager.addSignal(signalForBroadcast);
                    signal = signalForBroadcast;
                } else if (signal != null)
                    stopTransmission();
            } else {
                if (signal != null)
                    stopTransmission();
                noSignal = true;
            }
        } else if (signal != null) {
            stopTransmission();
        }
        tooltip.add(Component.literal("Signal - "+((signal != null) ? "BROADCASTING" : (noSignal?"NO SIGNAL":"OFFLINE"))));
        this.setTooltip(tooltip);
        super.tick();
    }
    public void stopTransmission() {
        endSignal(new Object());
        SignalManager.stopSignal(signal);
        signal = null;
    }
}
