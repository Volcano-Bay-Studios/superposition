package org.modogthedev.superposition.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaManager;

public class AntennaActorBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    public Antenna antenna;
    public AntennaActorBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void tick() {
        super.tick();
        if (antenna == null) {
            Antenna getAntenna = AntennaManager.getAmplifierAntenna(level,worldPosition);
            if (getAntenna != null)
                antenna = getAntenna;
        }
    }
    public void updateAntenna() {
        AntennaManager.antennaPartUpdate(level,worldPosition.above());
    }
}
