package org.modogthedev.superposition.system.widget;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.modogthedev.superposition.core.SuperpositionWidgets;
import org.modogthedev.superposition.system.cable.PortConfig;

import java.util.*;

/**
 * The widgets of a {@link WidgetHost}.
 */
public class WidgetContainer {
    private final Map<UUID, Widget> widgets = new HashMap<>();

    /**
     * @param offsetX How far the widgets of the container are from this one across the surface, in pixels
     */
    public record Neighbour(WidgetContainer container, int offsetX, int offsetY) {
    }

    public Collection<Widget> getWidgets() {
        return widgets.values();
    }

    public boolean isEmpty() {
        return widgets.isEmpty();
    }

    public int size() {
        return widgets.size();
    }

    public void clear() {
        widgets.clear();
    }

    public void buildPorts(PortConfig.Builder builder) {
        for (Widget widget : widgets.values()) {
            widget.buildPorts(builder);
        }
    }

    /**
     * @return True if the widgets are dirty and need to be networked to the client.
     */
    public boolean tick(Level level, WidgetHost host) {
        boolean update = false;
        int i = 0;
        for (Widget widget : widgets.values()) {
            if (widget.tick(level, host, i)) {
                update = true;
            }
            i++;
        }
        return update;
    }

    /**
     * Removes every widget that is no longer on the surface.
     *
     * @return The widgets that were removed
     */
    public List<Widget> removeOutOfBounds(WidgetSurface surface) {
        List<Widget> removed = new ArrayList<>();
        for (Widget widget : widgets.values()) {
            if (!surface.contains(widget.getPosition(), widget)) {
                removed.add(widget);
            }
        }
        for (Widget widget : removed) {
            widgets.remove(widget.getUuid());
        }
        return removed;
    }

    /**
     * @param pos A position on the surface
     */
    public @Nullable Widget getHit(Vector3d pos) {
        Widget targeted = null;
        for (Widget widget : widgets.values()) {
            Vector2i widgetPos = widget.getPosition();
            double minX = widgetPos.x / 16f;
            double minZ = widgetPos.y / 16f;
            if (pos.x > minX && pos.x < minX + widget.getBounds().x &&
                    pos.y > -1 / 64f && pos.y < widget.getBounds().y + 1 / 64f &&
                    pos.z > minZ && pos.z < minZ + widget.getBounds().z) {
                targeted = widget;
            }
        }
        return targeted;
    }

    /**
     * Checks that a widget would be on the surface and not overlap any other widget.
     */
    public boolean canPlace(Widget widget, Vector2i pos, WidgetSurface surface, List<Neighbour> neighbours) {
        if (!surface.contains(pos, widget)) {
            return false;
        }
        if (overlaps(widget, pos, 0, 0)) {
            return false;
        }
        for (Neighbour neighbour : neighbours) {
            if (neighbour.container.overlaps(widget, pos, neighbour.offsetX, neighbour.offsetY)) {
                return false;
            }
        }
        return true;
    }

    private boolean overlaps(Widget widget, Vector2i pos, int offsetX, int offsetY) {
        int maxX = pos.x + (int) (widget.getBounds().x * 16);
        int maxY = pos.y + (int) (widget.getBounds().z * 16);
        for (Widget other : widgets.values()) {
            int otherMinX = other.getPosition().x + offsetX;
            int otherMinY = other.getPosition().y + offsetY;
            int otherMaxX = otherMinX + (int) (other.getBounds().x * 16);
            int otherMaxY = otherMinY + (int) (other.getBounds().z * 16);
            if (pos.x < otherMaxX && maxX > otherMinX && pos.y < otherMaxY && maxY > otherMinY) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param widget The registered widget to place a copy of
     */
    public Widget place(Widget widget, Vector2i pos) {
        Widget newWidget = widget.makeClone();
        newWidget.setPosition(pos);
        widgets.put(newWidget.getUuid(), newWidget);
        return newWidget;
    }

    public boolean remove(Widget widget) {
        return widgets.remove(widget.getUuid()) != null;
    }

    public void addConfiguration(WidgetHost host, @Nullable Player player, @Nullable Widget only) {
        for (Widget widget : widgets.values()) {
            if (only == null || widget.equals(only)) {
                widget.addConfiguration(host, player);
            }
        }
    }

    public void save(CompoundTag tag) {
        ListTag widgetListTag = new ListTag();
        for (Widget widget : widgets.values()) {
            CompoundTag widgetTag = new CompoundTag();
            ResourceLocation location = widget.getLocation();
            widgetTag.putString("namespace", location.getNamespace());
            widgetTag.putString("path", location.getPath());
            widget.write(widgetTag);
            widgetListTag.add(widgetTag);
        }
        tag.put("widgets", widgetListTag);
    }

    public void load(CompoundTag tag) {
        if (!tag.contains("widgets")) {
            return;
        }
        ListTag widgetListTag = tag.getList("widgets", 10);
        int size = widgetListTag.size();
        if (size != widgets.size()) {
            widgets.clear();
            for (int i = 0; i < size; i++) {
                CompoundTag widgetTag = widgetListTag.getCompound(i);
                ResourceLocation location = ResourceLocation.fromNamespaceAndPath(widgetTag.getString("namespace"), widgetTag.getString("path"));
                Widget registered = SuperpositionWidgets.WIDGET.asVanillaRegistry().get(location);
                if (registered == null) {
                    continue;
                }
                Widget widget = registered.makeClone();
                if (widget != null) {
                    widget.read(widgetTag);
                    widgets.put(widget.getUuid(), widget);
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                CompoundTag widgetTag = widgetListTag.getCompound(i);
                Widget widget = widgets.get(widgetTag.getUUID("uuid"));
                if (widget != null) {
                    widget.read(widgetTag);
                }
            }
        }
    }

    /**
     * Load data that was sent from a client to modify the widgets.
     */
    public void loadSynced(CompoundTag tag) {
        for (Widget widget : widgets.values()) {
            if (tag.contains("widget-" + widget.getUuid())) {
                widget.loadSyncedData(tag.getCompound("widget-" + widget.getUuid()));
            }
        }
    }
}
