package org.modogthedev.superposition.blockentity;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.AreaLight;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.TickableBlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SignalGeneratorBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    private static final Logger log = LoggerFactory.getLogger(SignalGeneratorBlockEntity.class);
    Vec3 pos = new Vec3(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
    private float frequency;
    public float dial = 0;
    Signal connectedSignal;
    public boolean transmitting;
    private AreaLight light;


    public SignalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.SIGNAL_GENERATOR.get(), pos, state);
    }


    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
        this.frequency = tag.getFloat("frequency");
        boolean animated = frequency > .7f;

        level.setBlock(getBlockPos(), getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap")).setValue(SignalGeneratorBlock.ON, animated), 2);
        updateSignal();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putFloat("frequency", frequency);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.frequency = tag.getFloat("frequency");
        super.loadAdditional(tag, registries);
    }

    @Override
    public void tick() {

        preTick();
        putSignal(getSignal());
        if (this.level.isClientSide) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Signal Generator Status:"));
            tooltip.add(Component.literal("Frequency - "+Mth.frequencyToHzReadable(frequency*100000)));

            float speed = Mth.getFromRange(64,0,.1f,3,frequency);
            if (frequency < .72f || frequency > 64) {
                speed = 0;
            }
            dial += speed;
            if (dial > 24) {
                dial = 0;
            }
            setTooltip(tooltip);
            super.tick();
        }
    }
    public void endSignal(Object object) {
        if (connectedSignal != null) {
            connectedSignal.endTime = connectedSignal.lifetime;
            connectedSignal.emitting = false;
            SignalManager.stopSignal(connectedSignal);
            connectedSignal = null;
        }
    }

    @Override
    public Signal getSignal() {
        updateSignal();
        return connectedSignal;
    }

    @Override
    public Signal createSignal(Object nextObject) {
        updateSignal();
        return connectedSignal;
    }
    public float getFrequency() {
        return frequency;
    }



    public void updateSignal() {
        if (connectedSignal == null || !connectedSignal.emitting)
            connectedSignal = new Signal(pos,level,frequency*100000,1,frequency);
        else {
            connectedSignal.level = level;
            connectedSignal.frequency = frequency*100000;
            connectedSignal.sourceFrequency = frequency;
            connectedSignal.amplitude = 1;
            connectedSignal.clearEncodedData();
        }
    }
}
