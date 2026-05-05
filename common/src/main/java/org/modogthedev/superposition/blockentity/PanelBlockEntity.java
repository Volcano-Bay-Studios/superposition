package org.modogthedev.superposition.blockentity;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionWidgets;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.system.widget.Widget;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

import java.util.ArrayList;
import java.util.List;

public class PanelBlockEntity extends SignalActorBlockEntity {
    private final List<Widget> widgets = new ArrayList<>();

    private float frontHeight;
    private float backHeight;
    private float angle;

    public PanelBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.PANEL.get(), pos, state);
    }

    @Override
    public void tick() {
        resetTooltip();
        super.tick();
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag widgetListTag = new ListTag();
        for (Widget widget : widgets) {
            CompoundTag widgetTag = new CompoundTag();
            ResourceLocation location = widget.getLocation();
            widgetTag.putString("namespace", location.getNamespace());
            widgetTag.putString("path", location.getPath());
            widget.write(widgetTag);
            widgetListTag.add(widgetTag);
        }
        tag.put("widgets",widgetListTag);
        tag.putFloat("front_height",frontHeight);
        tag.putFloat("back_height",backHeight);
        updateAngle();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ListTag widgetListTag = tag.getList("widgets", 10);
        for (int i = 0; i < widgetListTag.size(); i++) {
            CompoundTag widgetTag = widgetListTag.getCompound(i);
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(widgetTag.getString("namespace"),widgetTag.getString("path"));
            Widget widget = SuperpositionWidgets.WIDGET.asVanillaRegistry().get(location);
            if (widget != null){
                widget.read(widgetTag);
                widgets.add(widget);
            }
        }

        if (tag.contains("front_height")) {
            frontHeight = tag.getFloat("front_height");
        }
        if (tag.contains("back_height")) {
            backHeight = tag.getFloat("back_height");
        }
        updateAngle();
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        if (tag.contains("front_height")) {
            frontHeight = tag.getFloat("front_height");
            frontHeight = Mth.clamp(frontHeight,0,5);
        }
        if (tag.contains("back_height")) {
            backHeight = tag.getFloat("back_height");
            backHeight = Mth.clamp(backHeight,0,5);
        }
        updateAngle();
        updateHeight(true);
        updateHeight(false);
    }

    @Override
    public void setupConfigTooltips() {
        super.setupConfigTooltips();
        this.addConfigTooltip("Front - " + frontHeight, () -> {
            CompoundTag tag = new CompoundTag();
            frontHeight += 1;
            if (frontHeight > 5) {
                frontHeight = 0;
            }
            updateAngle();
            updateHeight(true);
            updateHeight(false);
            tag.putFloat("front_height", frontHeight);
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, this.getBlockPos()));
        });

        this.addConfigTooltip("Back - " + backHeight, () -> {
            CompoundTag tag = new CompoundTag();
            backHeight += 1;
            if (backHeight > 5) {
                backHeight = 0;
            }
            updateAngle();
            updateHeight(true);
            updateHeight(false);
            tag.putFloat("back_height", backHeight);
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, this.getBlockPos()));
        });
    }

    public void updateHeight(boolean right) {
        Direction dir = getBlockState().getValue(SignalActorTickingBlock.FACING);
        BlockEntity blockEntity = level.getBlockEntity(getBlockPos().relative(right ? dir.getClockWise() : dir.getCounterClockWise()));
        if (blockEntity instanceof PanelBlockEntity panelBlockEntity) {
            if (panelBlockEntity.getBlockState().getValue(SignalActorTickingBlock.FACING).equals(dir)) {
                panelBlockEntity.frontHeight = this.frontHeight;
                panelBlockEntity.backHeight = this.backHeight;
                panelBlockEntity.markDirty();
                panelBlockEntity.updateHeight(right);
            }
        }
        updateAngle();
    }

    public void updateAngle() {
        angle = (float) Math.asin((getFrontHeight() - getBackHeight()) / 12);
    }

    public float getFrontHeight() {
        return frontHeight;
    }

    public float getBackHeight() {
        return backHeight;
    }

    public float getAngle() {
        return angle;
    }

    public List<Widget> getWidgets() {
        return widgets;
    }
}
