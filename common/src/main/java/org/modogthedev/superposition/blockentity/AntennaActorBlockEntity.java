package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.antenna.AntennaManager;
import org.modogthedev.superposition.util.TickableBlockEntity;

public class AntennaActorBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    int sleep = 0;
    public Antenna antenna;

    public AntennaActorBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public String classifyAntenna() {
        return "Unknown";
    }

    public float getBounusFrequency() {
        BlockPos sidedPos = getInvertedSwappedPos();
        if (sidedPos != null) {
            BlockEntity blockEntity = level.getBlockEntity(sidedPos);
            if (blockEntity instanceof SignalGeneratorBlockEntity signalGeneratorBlockEntity) {
                if (signalGeneratorBlockEntity.getSwappedPos().equals(getBlockPos())) {
                    return signalGeneratorBlockEntity.getFrequency() * 100000;
                }
            }
        }
        return 0;
    }

    @Override
    public void tick() {
        if (antenna == null) {
            Antenna getAntenna = AntennaManager.getAntennaActorAntenna(level, worldPosition);
            if (getAntenna != null)
                antenna = getAntenna;
        }
        if (sleep > 0)
            sleep--;
        super.tick();
    }

    public void removeAntenna() {
        antenna = null;
        update();
    }

    public void update() {
        level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    }

    public void setAntenna(Antenna antenna) {
        this.antenna = antenna;
    }

    public void updateAntenna() {
        Antenna getAntenna = AntennaManager.getAntennaActorAntenna(level, worldPosition);
        if (getAntenna != null)
            antenna = getAntenna;
    }
}
