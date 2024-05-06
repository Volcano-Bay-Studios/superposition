package org.modogthedev.superposition.blockentity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.block.ModulatorBlock;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.ModBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.SignalActorBlockEntity;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;

public class SignalGeneratorBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    Vec3 pos = new Vec3(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
    public float frequency;
    Signal connectedSignal;
    public boolean transmitting;


    public SignalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.SIGNAL_GENERATOR.get(), pos, state);
    }

    @Override
    public void writeData(CompoundTag tag) {
        super.writeData(tag);
        this.frequency = tag.getFloat("frequency");
        boolean animated = frequency > .7f;

        level.setBlock(getBlockPos(), getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap")).setValue(SignalGeneratorBlock.ON, animated), 2);
//        getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap"));
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
        if (connectedSignal != null && !transmitting)
            endSignal();
        if (connectedSignal == null && transmitting)
            updateSignal();

    }

    public void endSignal() {
        if (connectedSignal != null) {
            connectedSignal.endTime = connectedSignal.lifetime;
            connectedSignal.emitting = false;
            connectedSignal = null;
        }
    }

    @Override
    public Signal getSignal(Object nextCall) {
        return new Signal(pos,level,frequency,0);
    }

    @Override
    public Signal createSignal(Object nextObject) {
        if (connectedSignal == null)
            updateSignal();
        return connectedSignal;
    }

    public void updateSignal() {
        Signal signal = new Signal(pos, level, frequency, 0);
        endSignal();
        connectedSignal = signal;
    }

}
