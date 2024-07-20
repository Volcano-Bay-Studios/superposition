package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntity;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorBlockEntity;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class AmplifierBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    Vec3 pos = new Vec3(this.getBlockPos().getX(),this.getBlockPos().getY(),this.getBlockPos().getZ());
    public float modRate;
    public float redstoneMod;
    public float temp = 26;
    public float amplitude;
    public float lastAmplitude;
    boolean updateNext = false;
    public int ticks = 0;
    public int step = 0;
    public AmplifierBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntity.AMPLIFIER.get(), pos, state);
    }
    @Override
    public void writeData(CompoundTag tag) {
        super.writeData(tag);
        this.modRate = tag.getFloat("modRate");
        this.redstoneMod = tag.getFloat("redstoneMod");

        level.setBlock(getBlockPos(),getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES,tag.getBoolean("swap")),2);
//        getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap"));
    }
    public static float getRedstoneOffset(Level level, BlockPos pos) {
        return level.getSignal(pos,level.getBlockState(pos).getValue(AmplifierBlock.FACING).getOpposite());
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putFloat("modRate", modRate);
        pTag.putFloat("redstoneMod",redstoneMod);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.modRate = pTag.getFloat("modRate");
        this.redstoneMod = pTag.getFloat("redstoneMod");
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        if (signal != null) {
            signal.amplitude += (modRate + (getRedstoneOffset(level, getBlockPos()) * ((float) redstoneMod / 15)));
            if (amplitude != signal.amplitude)
                level.updateNeighbourForOutputSignal(getBlockPos(),getBlockState().getBlock());
            if (updateTooltip)
                amplitude = signal.amplitude;
        }
        return signal;
    }

    @Override
    public void tick() {
        preTick();
        if (level.isClientSide) {
            List<Component> tooltip = new ArrayList<>();
            this.setTooltip(tooltip);
            if (amplitude>0) {
                ticks++;
                if (ticks >Math.random()*10+10) {
                    step = (int) (Math.random()*3);
                    ticks = 0;
                }
                addTooltip(Component.literal("Amplifier Status: "));
                addTooltip(Component.literal("Amplitude - "+Math.floor(amplitude*10)/10));
            } else {
                ticks = -1;
                addTooltip(Component.literal("No Signal"));
            }
            if (temp > 26.1)
                addTooltip(Component.literal("Temperature - "+Math.floor(temp*10)/10+"°C"));
        }
        float tempGoal = (amplitude/10f)+26;
        if (temp>(tempGoal+.1f)) {
            temp -= .01f;
        } else if (temp<(tempGoal-.1f)) {
            temp += .05f;
        }
        if (lastAmplitude != amplitude)
            updateNext = true;
        if (updateNext) {
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            updateNext = true;
        }

        lastAmplitude = amplitude;
        amplitude = 0;
        super.tick();
    }

    @Override
    public BlockPos getDataPos() {
        return getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getOpposite(), 1);
    }

}