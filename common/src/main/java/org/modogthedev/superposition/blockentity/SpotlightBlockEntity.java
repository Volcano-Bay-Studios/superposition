package org.modogthedev.superposition.blockentity;

import foundry.veil.api.client.render.light.AreaLight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
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

    public int color = 0xffffff;

    @Override
    public void tick() {
        List<Component> tooltip = new ArrayList<>();
        setTooltip(tooltip);
        Signal signal = getSignal();
        if (signal != null && signal.getEncodedData() != null) {
            color = signal.getEncodedData().intValue();
        }
        if (light instanceof AreaLight areaLight) {
            this.configureAreaLight(areaLight);
        }
        super.tick();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tag.putInt("color",color);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (tag.contains("color")) {
            color = tag.getInt("color");
        }
    }

    @Override
    public boolean lightEnabled() {
        return true;
    }

    @Override
    public void configureAreaLight(AreaLight light) {
        super.configureAreaLight(light);
        Direction facing = this.getBlockState().getValue(SignalActorTickingBlock.FACING);
        BlockPos relativePos = BlockPos.containing(0,0,0).relative(facing,1);
        Vec3 relative = new Vec3(relativePos.getX(),relativePos.getY(),relativePos.getZ()).normalize().scale(0.99f);
        Vec3 center = this.getBlockPos().getCenter().add(relative.scale(0.5f)).subtract(0,1/16f,0);
        light.setPosition(center.x, center.y, center.z);
        light.setSize(5/16f, 5/16f);
        light.setColor(color);
        light.setDistance(30f);
        Color color1 = new Color(color);
        float brightness = (color1.getRed()/255f)+(color1.getGreen()/255f)+(color1.getBlue()/255f);
        light.setBrightness((float) (2f-(brightness/3f)));
    }
}
