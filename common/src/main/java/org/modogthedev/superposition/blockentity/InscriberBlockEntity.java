package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.util.TickableBlockEntity;

public class InscriberBlockEntity extends SignalActorBlockEntity implements TickableBlockEntity {

    public InscriberBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.INSCRIBER.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
    }

    @Override
    public void setupConfigTooltips() {
        super.setupConfigTooltips();
//        this.addConfigTooltip("Append Data - " + appendData, () -> {
//            CompoundTag tag = new CompoundTag();
//            appendData = !appendData;
//            tag.putBoolean("appendData", appendData);
//            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, this.getBlockPos()));
//        });
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            resetTooltip();
            addTooltip(Component.literal("No Information"));
        }
        super.tick();
    }
}


