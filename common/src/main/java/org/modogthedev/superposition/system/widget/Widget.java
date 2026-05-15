package org.modogthedev.superposition.system.widget;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.core.SuperpositionWidgetRenderers;
import org.modogthedev.superposition.core.SuperpositionWidgets;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.signal.Signal;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Widget implements Cloneable {
    private ResourceLocation location = null;
    private String name = "deviceName";
    private String color = "0xff3333";
    private Map<String,Consumer<String>> editable = new HashMap<>();

    private Vector2i position = new Vector2i();

    public Widget() {
        super();
    }

    @Nullable
    public static <T extends Widget> WidgetRenderer<T> getRenderer(T widget) {
        ResourceLocation location = widget.getLocation();
        //noinspection unchecked
        return (WidgetRenderer<T>) SuperpositionWidgetRenderers.WIDGET_RENDERER.asVanillaRegistry().get(location);
    }

    public ResourceLocation getLocation() {
        if (location == null) {
            location = SuperpositionWidgets.WIDGET.asVanillaRegistry().getKey(this);
            if (location != null) {
                name = location.getPath();
            }
        }
        return location;
    }

    public void setPosition(Vector2i position) {
        this.position.set(position);
    }

    public Vector2i getPosition() {
        return position;
    }

    public Vector3f getBounds() {
        return new Vector3f(0);
    }


    public PortConfig.Builder buildPorts(PortConfig.Builder builder) {
        builder.setPrefix(name + "-");
        return builder;
    }

    public List<Signal> getPortSignals(String port, PanelBlockEntity panel) {
        return panel.getPortSignals(name + "-" + port);
    }


    public void putPortSignals(String port, List<Signal> signals, PanelBlockEntity panel) {
        panel.putPortSignals(name + "-" + port, signals);
    }

    // Interaction and Behavior

    /**
     * Called every tick for each widget.
     * @param level The level of the widget, on both client and server.
     * @param panel The panel block entity that holds this widget.
     * @param index This widgets index, can be used to network data to the server.
     * @return True if the widgets data is dirty and needs to be networked to the client.
     */
    public boolean tick(Level level, PanelBlockEntity panel, int index) {
        return false;
    }

    public void hover(Vector3f relativePosition, Player player) {

    }

    /**
     * Called when the player left-clicks the widget.
     *
     * @param alt   If the shift key is pressed
     * @param level The level that is used
     * @param hit   The local position that was pressed
     * @return If the original event should be intercepted. You must return true on the client, or the interaction will not be networked.
     */
    public boolean leftClickInteract(boolean alt, Level level, Vector3f hit) {
        return false;
    }


    /**
     * Called when the player right-clicks the widget.
     *
     * @param alt   If the shift key is pressed
     * @param level The level that is used
     * @param hit   The local position that was pressed
     * @return If the original event should be intercepted.
     */
    public boolean rightClickInteract(boolean alt, Level level, Vector3f hit) {
        return false;
    }

    /**
     * Write data on the server to both send to clients, and to save.
     */
    public void write(CompoundTag tag) {
        tag.putString("name", name);
        tag.putInt("x", position.x);
        tag.putInt("y", position.y);
        tag.putString("color", color);
    }

    /**
     * Read data on the server and client to read from disk and read from network.
     */
    public void read(CompoundTag tag) {
        if (tag.contains("color")) {
            color = tag.getString("color");
        }
        if (tag.contains("name")) {
            name = tag.getString("name");
        }
        if (tag.contains("x")) {
            position.set(tag.getInt("x"), tag.getInt("y"));
        }
    }

    /**
     * Load data on the client that was sent from a client to modify this widget.
     */
    public void loadSyncedData(CompoundTag tag) {
        for (String key : editable.keySet()) {
            if (tag.contains(key)) {
                editable.get(key).accept(tag.getString(key));
            }
        }
    }
    public Color getColor(Color color) {
        Color widgetColor = getColor();
        return new Color((widgetColor.getRed()/255f) * (color.getRed()/255f), (widgetColor.getGreen()/255f) * (color.getGreen()/255f), (widgetColor.getBlue()/255f) * (color.getBlue()/255f), (widgetColor.getAlpha()/255f) * (color.getAlpha()/255f));
    }

    private Color getColor() {
        try {
            int colorI = NumberUtils.createNumber(this.color).intValue();
            return new Color(colorI);
        } catch (NumberFormatException ignored) {
        }
        return new Color(1f, 1f, 1f, 1f);
    }

    public void addConfiguration(PanelBlockEntity panel, int index, Player player) {
        addEditable(panel, "Name", index, () -> name, (s -> this.name = s));
        addEditable(panel,"Color", index, () -> color, (s -> color = s));
    }


    public void addEditable(PanelBlockEntity panel, String name, int index, Supplier<String> read, Consumer<String> set) {
        panel.addEditableTaggedConfigTooltip(name, "widget-" + index, read, set);
        editable.put(name,set);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        getLocation();
        return super.clone();
    }

    public Widget makeClone() {
        try {
            Widget clone = (Widget) clone();
            clone.position = (Vector2i) position.clone();
            return clone;
        } catch (CloneNotSupportedException ignored) {

        }
        return null;
    }
}
