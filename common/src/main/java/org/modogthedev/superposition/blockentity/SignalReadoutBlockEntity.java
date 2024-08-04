package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class SignalReadoutBlockEntity  extends SignalActorBlockEntity implements TickableBlockEntity {
    public SignalReadoutBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.SIGNAL_READOUT.get(), pos, state);
    }
    private BlockPos linkedPos = null;
    public Signal[] signals = new Signal[12];
    public float highestValue = 0;
    public float lowestValue = 0;
    @Override
    public void tick() {
        preTick();
        List<Component> tooltip = new ArrayList<>();
        setTooltip(tooltip);
        List<Signal> frequencySorted = getSignals();

        if (frequencySorted.isEmpty() && linkedPos != null && getLevel().getBlockEntity(linkedPos) instanceof SignalActorBlockEntity signalActorBlockEntity)
            frequencySorted = signalActorBlockEntity.getSignals();
        if (frequencySorted != null) {
            frequencySorted.sort((o1, o2) -> {
                if (o1.frequency == o2.frequency)
                    return 0;
                if (o1.frequency < o2.frequency)
                    return -1;
                else
                    return 1;
            });
            List<Signal> amplitudeSorted = new ArrayList<>(frequencySorted);
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
                    lowestValue = lowestValue / 2;
            }
            while (amplitudeSorted.size() > 12) {
                amplitudeSorted.remove(amplitudeSorted.get(amplitudeSorted.size() - 1));
                frequencySorted.remove(frequencySorted.get(frequencySorted.size() - 1));
            }
            signals = frequencySorted.toArray(new Signal[12]);
        }
        super.tick();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        if (linkedPos != null) {
            pTag.putInt("x", linkedPos.getX());
            pTag.putInt("y", linkedPos.getY());
            pTag.putInt("z", linkedPos.getZ());
        }
        super.saveAdditional(pTag);
    }
    public void loadLinkedPos(CompoundTag pTag) {
        linkedPos = new BlockPos(pTag.getInt("x"),pTag.getInt("y"),pTag.getInt("z"));
    }

    @Override
    public void load(CompoundTag pTag) {
        linkedPos = new BlockPos(pTag.getInt("x"),pTag.getInt("y"),pTag.getInt("z"));
        super.load(pTag);
    }
}
