package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.Superposition;
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
        if (antenna.avg.x == 0 && antenna.avg.z == 0)
            return "Monopole";
        if ((antenna.avg.x != 0 && antenna.avg.z == 0) || (antenna.avg.z != 0 && antenna.avg.x == 0))
            return "Dipole";
        return "Unknown";
    }

    @Override
    public void tick() {
        if (Superposition.DEBUG) {
            int maxDist = 1;
            for (Antenna antenna : AntennaManager.getAntennaList(level)) {
                for (float i = 0; i < 361; i += 10f) {
                    level.addParticle(ParticleTypes.WAX_ON, antenna.antennaActor.getCenter().x + (Math.sin(i) * maxDist),antenna.antennaActor.getCenter().y, antenna.antennaActor.getCenter().z + (Math.cos(i) * maxDist), 0, 0, 0);
                }
            }
        }
        if (antenna == null) {
            Antenna getAntenna = AntennaManager.getAntennaActorAntenna(level,worldPosition);
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
        Antenna getAntenna = AntennaManager.getAntennaActorAntenna(level,worldPosition);
           if (getAntenna != null)
            antenna = getAntenna;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
    }
}
