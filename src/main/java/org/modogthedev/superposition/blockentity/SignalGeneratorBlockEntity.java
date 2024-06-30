package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.SignalActorBlockEntity;
import org.modogthedev.superposition.util.TickableBlockEntity;

public class SignalGeneratorBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    Vec3 pos = new Vec3(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
    public float frequency;
    public float dial = 0;
    Signal connectedSignal;
    public boolean transmitting;


    public SignalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntity.SIGNAL_GENERATOR.get(), pos, state);
    }


    @Override
    public void writeData(CompoundTag tag) {
        super.writeData(tag);
        this.frequency = tag.getFloat("frequency");
        boolean animated = frequency > .7f;

        level.setBlock(getBlockPos(), getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap")).setValue(SignalGeneratorBlock.ON, animated), 2);
        updateSignal();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putFloat("frequency", frequency);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.frequency = pTag.getFloat("frequency");
    }



    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            float speed = Mth.getFromRange(64,0,.1f,3,frequency);
            if (frequency < .72f || frequency > 64) {
                speed = 0;
            }
            dial += speed;
            if (dial > 24) {
                dial = 0;
            }
            return;
        }
    }

    public void endSignal() {
        if (connectedSignal != null) {
            connectedSignal.endTime = connectedSignal.lifetime;
            connectedSignal.emitting = false;
            SignalManager.stopSignal(connectedSignal);
            connectedSignal = null;
        }
    }

    @Override
    public Signal getSignal(Object nextCall, boolean selfModulate) {
        updateSignal();
        return connectedSignal;
    }

    @Override
    public Signal createSignal(Object nextObject) {
        updateSignal();
        return connectedSignal;
    }

    public void updateSignal() {
        if (connectedSignal == null)
            connectedSignal = new Signal(pos,level,frequency,1);
        else {
            connectedSignal.pos = pos;
            connectedSignal.level = level;
            connectedSignal.frequency = frequency;
            connectedSignal.amplitude = 1;
        }
    }

    @Override
    public SignalActorBlockEntity topBE(Object nextCall) {
        return this;
    }
}
