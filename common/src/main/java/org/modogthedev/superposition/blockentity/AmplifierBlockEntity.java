package org.modogthedev.superposition.blockentity;

import foundry.veil.api.client.render.light.data.LightData;
import foundry.veil.api.client.render.light.data.PointLightData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.block.AmplifierBlock;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionTags;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class AmplifierBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {

    public float amplification;
    public float redstoneAmplification;
    public float temp = 26.1f;
    public float amplitude;
    public float throttle = 0f;
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
        this.amplification = tag.getFloat("amplification");
        this.redstoneAmplification = tag.getFloat("redstoneAmplification");

        level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap")), 2);
//        getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap"));
    }

    public static float getRedstoneOffset(Level level, BlockPos pos) {
        return level.getSignal(pos, level.getBlockState(pos).getValue(AmplifierBlock.FACING).getOpposite());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putFloat("amplification", amplification);
        tag.putFloat("redstoneAmplification", redstoneAmplification);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.amplification = tag.getFloat("amplification");
        this.redstoneAmplification = tag.getFloat("redstoneAmplification");
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        if (signal != null) {
            signal.addAmplitude(Math.max(0, amplification - throttle + (getRedstoneOffset(level, this.getBlockPos()) * (this.redstoneAmplification / 15))));
            amplitude += signal.getAmplitude();
        }
        return signal;
    }

    public float getColdness(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.AIR)) {
            return 0.0F;
        }
        if (state.is(SuperpositionTags.COOL)) {
            return 1.5f;
        }
        if (state.is(SuperpositionTags.COLD)) {
            return 3f;
        }
        if (state.is(SuperpositionTags.VERY_COLD)) {
            return 5f;
        }
        return 0.0F;
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            List<Component> tooltip = new ArrayList<>();
            this.setTooltip(tooltip);
            for (Signal signal : putSignals) {
                amplitude += signal.getAmplitude();
            }
            if (amplitude > 0) {
                ticks++;
                if (ticks > ticksToChange - 1) {
                    lastStep = step;
                    step = (int) (Math.random() * 3);
                    ticks = 0;
                }
                this.addTooltip(Component.literal("Amplifier Status: "));
                this.addTooltip(Component.literal("Amplitude - " + Math.floor(Mth.map(amplitude, 0, 153, 3, 10) * 10f) / 10f + "dBi"));
                if (temp >= 26) {
                    this.addTooltip(Component.literal("Temperature - " + Math.floor(temp * 10) / 10 + "°C"));
                    this.addTooltip(Component.literal("Throttling - " + Math.floor(Mth.map(throttle, 0, 153, 0, 10) * 10f) / 10f + "dBi"));
                } else {
                    this.addTooltip(Component.literal("Temperature - " + Math.floor(temp * 10) / 10 + "°C"));
                    this.addTooltip(Component.literal("Overclocking - " + Math.floor(Mth.map(-throttle, 0, 153, 0, 10) * 10f) / 10f + "dBi"));
                }
            } else {
                ticks = -1;
                this.addTooltip(Component.literal("No Signal"));
            }

        }
        float coldness = 0f;
        for (Direction direction : Direction.values()) {
            coldness += getColdness(getBlockPos().relative(direction));
        }

        float tempGoal = (amplitude / 10f) + 26;
        tempGoal -= coldness;
        if (tempGoal > temp) {
            temp += (tempGoal - temp) / 100f;
        } else if (tempGoal < temp) {
            temp += (tempGoal - temp) / 500f;
        }
        throttle = (temp - 26f) * 10f;
        amplitude -= throttle;
        amplitude = Math.max(amplitude, 0);

        if (updateNext) {
            level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
            updateNext = false;
        }
        if (lastAmplitude != amplitude) {
            updateNext = true;
        }
        lastAmplitude = amplitude;
        amplitude = 0;
        super.tick();
    }

    public boolean lightEnabled() {
        return true;
    }

    @Override
    public LightData prepareLight() {
        return new PointLightData();
    }

    @Override
    public void configurePointLight(PointLightData light) {
        Vec3 center = this.getBlockPos().getCenter();
        Direction facing = this.getBlockState().getValue(SignalActorTickingBlock.FACING);
        center = center.add(new Vec3(facing.getNormal().getX(), facing.getNormal().getY(), facing.getNormal().getZ()).scale(0.4f));
        light.setPosition(center.x, center.y, center.z);
        light.setColor(0xc76528);
        light.setBrightness((float) Math.clamp((temp - 26.1f) / 4f, 0, 3));
        light.setRadius(2f);
    }

    @Override
    public boolean shouldUpdateLight() {
        return true;
    }
}