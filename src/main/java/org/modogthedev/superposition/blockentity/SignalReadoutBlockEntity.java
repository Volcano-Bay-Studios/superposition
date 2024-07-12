package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorBlockEntity;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class SignalReadoutBlockEntity  extends SignalActorBlockEntity implements TickableBlockEntity {
    public SignalReadoutBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntity.SIGNAL_READOUT.get(), pos, state);
    }
    public Signal[] signals = new Signal[14];
    public float highestValue = 0;
    public float lowestValue = 0;
    @Override
    public void tick() {
        preTick();
        List<Component> tooltip = new ArrayList<>();
        setTooltip(tooltip);
        List<Signal> frequencySorted = new ArrayList<>(getSignals());
        frequencySorted.sort((o1, o2) -> {
            if (o1.frequency == o2.frequency)
                return 0;
            if (o1.frequency < o2.frequency)
                return -1;
            else
                return 1;
        });
        List<Signal> amplitudeSorted = new ArrayList<>(getSignals());
        amplitudeSorted.sort((o1, o2) -> {
            if (o1.amplitude == o2.amplitude)
                return 0;
            if (o1.amplitude < o2.amplitude)
                return -1;
            else
                return 1;
        });
        if (!amplitudeSorted.isEmpty()) {
            highestValue = amplitudeSorted.get(amplitudeSorted.size() - 1).amplitude;
            lowestValue = amplitudeSorted.get(0).amplitude;
            if (amplitudeSorted.size() == 1)
                lowestValue = lowestValue/2;
        }
        while (amplitudeSorted.size()>14) {
            amplitudeSorted.remove(amplitudeSorted.get(amplitudeSorted.size()-1));
            frequencySorted.remove(frequencySorted.get(frequencySorted.size()-1));
        }
        signals = frequencySorted.toArray(new Signal[14]);

        super.tick();
    }
}
