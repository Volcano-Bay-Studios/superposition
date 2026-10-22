package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.modogthedev.superposition.client.renderer.ui.SuperpositionUITooltipRenderer;
import org.modogthedev.superposition.compat.sable.SableCompat;
import org.modogthedev.superposition.core.SuperpositionItems;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.widget.Widget;
import org.modogthedev.superposition.system.widget.WidgetContainer;
import org.modogthedev.superposition.system.widget.WidgetHost;
import org.modogthedev.superposition.system.widget.WidgetSurface;
import org.modogthedev.superposition.util.SignalHelper;
import org.modogthedev.superposition.util.WidgetUseResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A block that holds widgets. Each type of panel only has to describe the surface its widgets are on.
 */
public abstract class WidgetPanelBlockEntity extends SignalActorBlockEntity implements WidgetHost, Clearable {
    private final WidgetContainer widgets = new WidgetContainer();
    private Widget lastTargeted = null;

    public WidgetPanelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        rebuild();
    }

    /**
     * @return The surface widgets are placed on, relative to the block
     */
    public abstract WidgetSurface getSurface();

    /**
     * @param offsetX How far the panel is from this one across the surface, in pixels
     */
    public record Joined(WidgetPanelBlockEntity panel, int offsetX, int offsetY) {
    }

    /**
     * @return The panels that share a surface with this one. Widgets can reach onto these panels.
     */
    public List<Joined> getJoined() {
        return List.of();
    }

    public List<WidgetContainer.Neighbour> getNeighbours() {
        List<WidgetContainer.Neighbour> neighbours = new ArrayList<>();
        for (Joined joined : getJoined()) {
            neighbours.add(new WidgetContainer.Neighbour(joined.panel.widgets, joined.offsetX, joined.offsetY));
        }
        return neighbours;
    }

    /**
     * @param cameraHit A position in the level, on this panel
     * @return The panel that holds the widget at the position, which is a joined panel if its widget reaches onto this one
     */
    public @Nullable WidgetPanelBlockEntity getOwner(Vector3d cameraHit) {
        if (getHit(cameraHit) != null) {
            return this;
        }
        for (Joined joined : getJoined()) {
            if (joined.panel.getHit(cameraHit) != null) {
                return joined.panel;
            }
        }
        return null;
    }

    /**
     * Adds the configuration that is shown when no widget is targeted.
     */
    protected void addPanelConfiguration() {
    }

    @Override
    public PortConfig.Builder buildPorts(PortConfig.Builder builder) {
        // This is first called before the container exists
        if (widgets != null) {
            widgets.buildPorts(builder);
        }
        return builder;
    }

    public void rebuild() {
        buildPorts(getPortConfig().rebuild());
    }

    @Override
    public void tick() {
        resetTooltip();
        super.tick();

        if (widgets.tick(getLevel(), this)) {
            markDataDirty();
        }

        List<Widget> removed = widgets.removeOutOfBounds(getSurface());
        if (!removed.isEmpty()) {
            rebuild();
            dropWidgets(removed.size());
        }
    }

    private void dropWidgets(int count) {
        Vec3 center = getBlockPos().getCenter();
        ItemStack stack = SuperpositionItems.WIDGET.get().getDefaultInstance();
        stack.setCount(count);
        Containers.dropItemStack(level, center.x, center.y + 0.25f, center.z, stack);
    }

    public void dropOnRemove() {
        if (widgets.isEmpty()) {
            return;
        }
        dropWidgets(widgets.size());
        widgets.clear();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        widgets.save(tag);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (getLevel() != null && getLevel().isClientSide && SuperpositionUITooltipRenderer.editPos.equals(getBlockPos())) {
            return;
        }
        super.loadAdditional(tag, registries);
        widgets.load(tag);
        rebuild();
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        widgets.loadSynced(tag);
        rebuild();
        super.loadSyncedData(tag);
    }

    @Override
    protected boolean allowSwap() {
        return lastTargeted == null;
    }

    @Override
    public void setupConfigTooltips(Player player) {
        super.setupConfigTooltips(player);
        if (!player.level().isClientSide) {
            widgets.addConfiguration(this, null, null);
        } else if (lastTargeted != null) {
            widgets.addConfiguration(this, player, lastTargeted);
            lastTargeted = null;
        } else {
            addPanelConfiguration();
        }
    }

    public Collection<Widget> getWidgets() {
        return widgets.getWidgets();
    }

    public WidgetContainer getWidgetContainer() {
        return widgets;
    }

    /**
     * @param pos A position in the level
     * @return The position on the surface of this panel
     */
    public Vector3d toSurface(Vector3d pos) {
        Vec3 blockPos = Vec3.atLowerCornerOf(getBlockPos());
        return getSurface().toSurface(new Vector3d(pos).sub(blockPos.x, blockPos.y, blockPos.z));
    }

    public @Nullable Widget getHit(Vector3d cameraHit) {
        return widgets.getHit(toSurface(cameraHit));
    }

    public boolean hoverCamera(Vector3d cameraHit, boolean tryOther) {
        WidgetPanelBlockEntity owner = tryOther ? getOwner(cameraHit) : this;
        Widget hit = owner != null ? owner.getHit(cameraHit) : null;
        if (hit != null) {
            owner.lastTargeted = hit;
            return true;
        }
        return false;
    }

    /**
     * Places a copy of the given widget if it fits on the surface.
     *
     * @return If the widget was placed
     */
    public boolean placeWidget(Vector2i placement, Widget widget) {
        if (level.isClientSide || !widgets.canPlace(widget, placement, getSurface(), getNeighbours())) {
            return false;
        }
        widgets.place(widget, placement);
        rebuild();
        markDataDirty();
        return true;
    }

    public void removeWidget(Vec3 hit) {
        Vector3d cameraHit = new Vector3d(hit.x, hit.y, hit.z);
        WidgetPanelBlockEntity owner = getOwner(cameraHit);
        if (owner == null) {
            return;
        }
        Widget widget = owner.getHit(cameraHit);
        if (widget != null && owner.widgets.remove(widget)) {
            owner.rebuild();
            owner.dropWidgets(1);
        }
    }

    @Override
    public void clearContent() {
        widgets.clear();
    }

    // -----------INTERACTION-----------

    public WidgetUseResult primaryInteract(boolean alt, Vector3d cameraHit) {
        WidgetPanelBlockEntity owner = getOwner(cameraHit);
        if (owner != null && owner != this) {
            return owner.primaryInteract(alt, cameraHit);
        }
        Vector3d pos = toSurface(cameraHit);
        Widget widget = widgets.getHit(pos);
        if (widget != null) {
            pos.sub(widget.getPosition().x / 16f, 0, widget.getPosition().y / 16f);
            WidgetUseResult result = widget.leftClickInteract(alt, level, pos);
            if (result.consumesAction()) {
                markDataDirty();
            }
            return result;
        }
        return WidgetUseResult.PASS;
    }

    public WidgetUseResult secondaryInteract(boolean alt, Vector3d cameraHit) {
        WidgetPanelBlockEntity owner = getOwner(cameraHit);
        if (owner != null && owner != this) {
            return owner.secondaryInteract(alt, cameraHit);
        }
        Vector3d pos = toSurface(cameraHit);
        Widget widget = widgets.getHit(pos);
        if (widget != null) {
            pos.sub(widget.getPosition().x / 16f, 0, widget.getPosition().y / 16f);
            WidgetUseResult result = widget.rightClickInteract(alt, level, pos);
            if (result.consumesAction()) {
                markDataDirty();
            }
            return result;
        }
        return WidgetUseResult.PASS;
    }

    // -----------HOST-----------

    @Override
    public Vec3 getWidgetPosition() {
        return SableCompat.tryTransform(level, getBlockPos().getCenter());
    }

    @Override
    public Signal createSignal() {
        return SignalHelper.getEmptySignal(level, getBlockPos());
    }

    @Override
    public void playSound(SoundEvent sound, float pitch) {
        if (level != null) {
            level.playSound(null, getBlockPos(), sound, SoundSource.BLOCKS, 1, pitch);
        }
    }
}
