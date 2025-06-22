package org.modogthedev.superposition.blockentity;

import foundry.veil.api.client.render.light.data.AreaLightData;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorTickingBlock;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SpotlightBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    public SpotlightBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.SPOTLIGHT.get(), pos, state);
    }

    private int color = 0xffffff;
    private float iris = 30f;

    @Override
    public void tick() {
        List<Component> tooltip = new ArrayList<>();
        setTooltip(tooltip);
        Signal signal = getSignal();
        if (signal != null && signal.getEncodedData() != null) {
            color = signal.getEncodedData().intValue();
        }
        if (signal != null && signal.getEncodedData() != null && signal.getEncodedData().compoundTagData() != null) {
            if (signal.getEncodedData().compoundTagData().contains("iris", 3)) {
                iris = signal.getEncodedData().compoundTagData().getInt("iris");
                iris = Mth.clamp(iris, 3f, 30f);
            }
        }
        super.tick();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("color")) {
            color = tag.getInt("color");
        }
        if (tag.contains("iris")) {
            iris = tag.getFloat("iris");
            iris = Mth.clamp(iris, 5f, 30f);
        }
    }

    public int getColor() {
        return color;
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        if (tag.contains("iris")) {
            iris = tag.getFloat("iris");
            iris = Mth.clamp(iris, 3f, 30f);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("color", color);
        tag.putFloat("iris", iris);
    }

    @Override
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {

        return super.modulateSignal(signal, updateTooltip);
    }

    @Override
    public boolean lightEnabled() {
        return true;
    }

    @Override
    public boolean shouldUpdateLight() {
        return true;
    }

    @Override
    public void configureAreaLight(AreaLightData light) {
        super.configureAreaLight(light);
        Direction facing = this.getBlockState().getValue(SignalActorTickingBlock.FACING);
        BlockPos relativePos = BlockPos.containing(0, 0, 0).relative(facing, 1);
        Vec3 relative = new Vec3(relativePos.getX(), relativePos.getY(), relativePos.getZ()).normalize().scale(0.99f);
        Vec3 center = this.getBlockPos().getCenter().add(relative.scale(0.51f)).subtract(0, 1 / 16f, 0);
        light.getPosition().set(center.x, center.y, center.z);
        light.setSize(5 / 16f, 5 / 16f);
        light.setColor(color);
        light.setDistance(30f);
        light.setAngle(iris / 100f);
        Color color1 = new Color(color);
        float brightness = (color1.getRed() / 255f) + (color1.getGreen() / 255f) + (color1.getBlue() / 255f);
        light.setBrightness(2f - (brightness / 3f));
    }

    @Override
    public void setupConfigTooltips() {
        super.setupConfigTooltips();
        this.addConfigTooltip("Iris - " + iris, () -> {
            CompoundTag tag = new CompoundTag();
            iris -= 5f;
            if (iris <= 0f) {
                iris = 30f;
            }
            tag.putFloat("iris", iris);
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, this.getBlockPos()));
        });
    }
}
