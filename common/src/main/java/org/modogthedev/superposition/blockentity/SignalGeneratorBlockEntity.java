package org.modogthedev.superposition.blockentity;

import foundry.veil.api.client.render.light.data.AreaLightData;
import foundry.veil.api.client.render.light.data.LightData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SuperpositionMth;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class SignalGeneratorBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {

    private float frequency;
    public float dial = 0;
    Signal connectedSignal;
    public boolean transmitting;

    private final Vector3d pos = new Vector3d();

    public SignalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.SIGNAL_GENERATOR.get(), pos, state);
        this.pos.set(pos.getX(), pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
        this.frequency = tag.getFloat("frequency");
        boolean animated = frequency > .7f;

        this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap")).setValue(SignalGeneratorBlock.ON, animated), 2);
        this.updateSignal();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putFloat("frequency", frequency);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("frequency")) {
            this.frequency = Mth.clamp(tag.getFloat("frequency"),0,150);
        }
        super.loadAdditional(tag, registries);
    }

    @Override
    public void tick() {
        Signal signal = this.getSignal();
        if (signal != null) {
            this.putSignal(signal);
        }
        if (this.level.isClientSide()) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Signal Generator Status:"));
            tooltip.add(Component.literal("Frequency - " + SuperpositionMth.formatHz(frequency * 100000)));

            float speed = SuperpositionMth.getFromRange(150, 0, 3, .1f, frequency);
            if (frequency <= 0 || frequency > 150) {
                speed = 0;
                dial = 0;
            }
            dial += speed;
            if (dial > 24) {
                dial = 0;
            }
            this.setTooltip(tooltip);
        }
        super.tick();
    }

    @Override
    public Signal getSignal() {
        if (frequency <= 0 || frequency > 150) {
            return null;
        }
        this.updateSignal();
        return connectedSignal;
    }

    @Override
    public List<Signal> getSignals() {
        List<Signal> signals = new ArrayList<>();
        if (getSignal() != null) {
            signals.add(getSignal());
        }
        return signals;
    }

    public float getFrequency() {
        return frequency;
    }


    public void updateSignal() {
        if (connectedSignal == null || !connectedSignal.isEmitting()) {
            connectedSignal = new Signal(this.pos, level, frequency * 100000, 1, frequency);
        } else {
            connectedSignal.level = level;
            connectedSignal.setFrequency(frequency * 100000);
            connectedSignal.setSourceFrequency(frequency);
            connectedSignal.setAmplitude(1);
            connectedSignal.clearEncodedData();
        }
    }

    public boolean lightEnabled() {
        return true;
    }

    @Override
    public LightData prepareLight() {
        return new AreaLightData();
    }

    @Override
    public void configureAreaLight(AreaLightData light) {
        super.configureAreaLight(light);
        light.setSize(0.25f, 0.25f);
        light.setColor(3979870);
        light.setDistance(5f);
        light.setBrightness(0.75f);
    }
}
