package org.modogthedev.superposition.blockentity;

import foundry.veil.api.client.render.light.PointLight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class AmplifierBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {

    public float modRate;
    public float redstoneMod;
    public float temp = 26;
    public float amplitude;
    public float lastAmplitude;
    boolean updateNext = false;
    public int ticks = 0;
    public int step = 0;
    public int lastStep = 1;
    public static final int ticksToChange = 40;

    public AmplifierBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.AMPLIFIER.get(), pos, state);
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
        this.modRate = tag.getFloat("modRate");
        this.redstoneMod = tag.getFloat("redstoneMod");

        level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap")), 2);
//        getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap"));
    }

    public static float getRedstoneOffset(Level level, BlockPos pos) {
        return level.getSignal(pos, level.getBlockState(pos).getValue(AmplifierBlock.FACING).getOpposite());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putFloat("modRate", modRate);
        tag.putFloat("redstoneMod", redstoneMod);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.modRate = tag.getFloat("modRate");
        this.redstoneMod = tag.getFloat("redstoneMod");
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        if (signal != null) {
            signal.modulate(modRate + (getRedstoneOffset(level, this.getBlockPos()) * (this.redstoneMod / 15)));
            if (amplitude != signal.getAmplitude()) {
                level.updateNeighbourForOutputSignal(this.getBlockPos(), this.getBlockState().getBlock());
            }
            if (updateTooltip) {
                amplitude = signal.getAmplitude();
            }
        }
        return signal;
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            List<Component> tooltip = new ArrayList<>();
            this.setTooltip(tooltip);
            if (amplitude > 0) {
                ticks++;
                if (ticks > ticksToChange - 1) {
                    lastStep = step;
                    step = (int) (Math.random() * 3);
                    ticks = 0;
                }
                this.addTooltip(Component.literal("Amplifier Status: "));
                this.addTooltip(Component.literal("Amplitude - " + Math.floor(amplitude * 10) / 10));
            } else {
                ticks = -1;
                this.addTooltip(Component.literal("No Signal"));
            }
            if (temp > 26.1) {
                this.addTooltip(Component.literal("Temperature - " + Math.floor(temp * 10) / 10 + "°C"));
            }
        }
        float tempGoal = (amplitude / 10f) + 26;
        if (temp > (tempGoal + .1f)) {
            temp -= .01f;
        } else if (temp < (tempGoal - .1f)) {
            temp += .05f;
        }
        if (lastAmplitude != amplitude) {
            updateNext = true;
        }
        if (updateNext) {
            level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
            updateNext = true;
        }

        lastAmplitude = amplitude;
        amplitude = 0;
        if (light != null) {
            light.setBrightness((lastAmplitude/50f));
        }
        super.tick();
    }

    @Override
    public BlockPos getDataPos() {
        return this.getBlockPos().relative(this.level.getBlockState(this.getBlockPos()).getValue(SignalActorTickingBlock.FACING).getOpposite(), 1);
    }

    public boolean lightEnabled() {
        return true;
    }

    @Override
    public void createLight() {
        light = new PointLight();
    }

    @Override
    public void configurePointLight(PointLight light) {
        Vec3 center = this.getBlockPos().getCenter();
        Direction facing = this.getBlockState().getValue(SignalActorTickingBlock.FACING);
        center = center.add(new Vec3(facing.getNormal().getX(),facing.getNormal().getY(),facing.getNormal().getZ()).scale(0.4f));
        light.setPosition(center.x, center.y, center.z);
        light.setColor(3979870);
        light.setBrightness(1.5f);
        light.setRadius(3f);
    }
}