package org.modogthedev.superposition.blockentity;

import foundry.veil.api.client.render.light.AreaLight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SignalReadoutBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    public SignalReadoutBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.SIGNAL_READOUT.get(), pos, state);
    }

    private BlockPos linkedPos = null;
    public Signal[] signals = new Signal[12];
    public float highestValue = 0;
    public float lowestValue = 0;
    public List<String> text = new ArrayList<>();

    @Override
    public void tick() {
        preTick();
        List<Component> tooltip = new ArrayList<>();
        setTooltip(tooltip);
        List<Signal> frequencySorted = getSignals();
        text.clear();
        for (Signal signal : getSignals()) {
            EncodedData<? extends Serializable> encodedData = signal.getEncodedData();
            if (encodedData != null && encodedData.getObj() instanceof String s) {
                text.add(s);
            }
        }

        if (level.isClientSide) {
            if (frequencySorted.isEmpty() && linkedPos != null && getLevel().getBlockEntity(linkedPos) instanceof SignalActorBlockEntity signalActorBlockEntity)
                frequencySorted = signalActorBlockEntity.getSignals();
            if (frequencySorted != null) {
                frequencySorted.sort((o1, o2) -> Float.compare(o1.frequency, o2.frequency));
                List<Signal> amplitudeSorted = new ArrayList<>(frequencySorted);
                amplitudeSorted.sort((o1, o2) -> Float.compare(o1.amplitude, o2.amplitude));
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
        }
        super.tick();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (linkedPos != null) {
            tag.putInt("linkedPosx", linkedPos.getX());
            tag.putInt("linkedPosy", linkedPos.getY());
            tag.putInt("linkedPosz", linkedPos.getZ());
        }
        super.saveAdditional(tag, registries);
    }

    public void loadLinkedPos(CompoundTag pTag) {
        linkedPos = new BlockPos(
                pTag.getInt("linkedPosx"),
                pTag.getInt("linkedPosy"),
                pTag.getInt("linkedPosz")
        );
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        loadLinkedPos(tag);
        super.loadAdditional(tag, registries);
    }

    @Override
    public boolean lightEnabled() {
        return true;
    }

    @Override
    public void configureAreaLight(AreaLight light) {
        super.configureAreaLight(light);
        light.setSize(0.317,0.244);
        light.setColor(3979870);
        light.setDistance(10);
        light.setBrightness(1.5f);
    }
}
