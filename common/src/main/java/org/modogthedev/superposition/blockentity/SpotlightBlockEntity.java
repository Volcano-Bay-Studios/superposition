package org.modogthedev.superposition.blockentity;

import foundry.veil.api.client.render.light.AreaLight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.text.WordUtils;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;
import org.modogthedev.superposition.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SpotlightBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {
    public SpotlightBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.MONITOR.get(), pos, state);
    }

    @Override
    public void tick() {
        List<Component> tooltip = new ArrayList<>();
        setTooltip(tooltip);
        super.tick();
    }

    @Override
    public boolean lightEnabled() {
        return true;
    }

    @Override
    public void configureAreaLight(AreaLight light) {
        super.configureAreaLight(light);
        light.setSize(0.317, 0.244);
        light.setColor(3979870);
        light.setDistance(10);
        light.setBrightness(1.5f);
    }
}
