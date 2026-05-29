package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.signal.Signal;

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
        boolean isPowered = level.hasNeighborSignal(this.getBlockPos());
        if (antenna != null) {
            antennaExists = true;
            List<Signal> signals = getInputSignals();
            noSignal = signals.isEmpty();
            if (level.isClientSide) {

            } else {
                if (isPowered) {
                    for (Signal broadcastSignal : signals) {
                        broadcastSignal.level = level;
                    }
                    antenna.sendSignals(signals);
                } else {
                    antenna.sendSignals(List.of());
                }
            }
        }

        tooltip.add(Component.literal("Status - " + ((antennaExists) ? (noSignal ? "NO SIGNAL" : ( isPowered ? "BROADCASTING" : "DISABLED")) : "NO ANTENNA")));
        this.setTooltip(tooltip);
        super.tick();
    }
}
