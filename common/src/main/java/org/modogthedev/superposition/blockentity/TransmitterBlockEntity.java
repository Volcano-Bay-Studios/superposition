package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.ArrayList;
import java.util.List;

public class TransmitterBlockEntity extends AntennaActorBlockEntity {

    public TransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.TRANSMITTER.get(), pos, state);
    }

    @Override
    public void tick() {
        List<Component> tooltip = new ArrayList<>();
        BlockPos sidedPos = this.getSwappedPos();
        tooltip.add(Component.literal("Transmitter Status:"));
        if (antenna != null && level.isClientSide) {
            antenna.updateTooltip(tooltip);
        }
        boolean noSignal = false;
        boolean antennaExists = false;
        boolean isPowered = false;
        if (antenna != null) {
            antennaExists = true;
            List<Signal> signals = getSignals();
            noSignal = signals.isEmpty();
            if (level.isClientSide) {
                if (!signals.isEmpty()) {
                    tooltip.add(Component.literal(signals.size() == 1 ? "Signal: " : "Signals: "));
                    for (Signal broadcastSignal : signals) {
                        tooltip.add(Component.literal(SuperpositionMth.formatHz(broadcastSignal.getFrequency())));
                    }
                } else {
                    tooltip.add(Component.literal("No Signals"));
                }
            } else {
                if (level.hasNeighborSignal(this.getBlockPos())) { //TODO: borken
                    isPowered = true;
                    for (Signal broadcastSignal : signals) {
                        broadcastSignal.level = level;
                    }
                    antenna.sendSignals(signals);
                }
            }
        }

        tooltip.add(Component.literal("Status - " + ((antennaExists) ? (noSignal ? "NO SIGNAL" : ( isPowered ? "BROADCASTING" : "DISABLED")) : "NO ANTENNA")));
        this.setTooltip(tooltip);
        super.tick();
    }
}
