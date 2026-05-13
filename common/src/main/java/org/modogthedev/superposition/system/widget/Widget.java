package org.modogthedev.superposition.system.widget;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.core.SuperpositionWidgets;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Widget implements Cloneable {
    private ResourceLocation location = null;
    private String name = "deviceName";

    private Vector2i position = new Vector2i();

    public Widget() {
        super();
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


    public void getPortSignals(String port, List<Signal> signals, PanelBlockEntity panel) {
        panel.putPortSignals(name + "-" + port, signals);
    }

    // Interaction and Behavior
    public void tick(Level level, PanelBlockEntity panel) {
    }

    public void hover(Vector3f relativePosition, Player player) {

    }


    /**
     * Called when the player left clicks the widget.
     * @param alt If the shift key is pressed
     * @param level The level that is used
     * @param hit The local position that was pressed
     * @return If the original event should be intercepted. You must return true on the client, or the interaction will not be networked.
     */
    public boolean leftClickInteract(boolean alt, Level level, Vector3f hit) {
        return false;
    }


    /**
     * Called when the player right clicks the widget.
     * @param alt If the shift key is pressed
     * @param level The level that is used
     * @param hit The local position that was pressed
     * @return If the original event should be intercepted.
     */
    public boolean rightClickInteract(boolean alt, Level level, Vector3f hit) {
        return false;
    }


    public void write(CompoundTag tag) {
        tag.putString("name", name);
        tag.putInt("x", position.x);
        tag.putInt("y", position.y);
    }

    public void read(CompoundTag tag) {
        if (tag.contains("name")) {
            name = tag.getString("name");
        }
        if (tag.contains("x")) {
            position.set(tag.getInt("x"), tag.getInt("y"));
        }
    }

    public void loadEditable(CompoundTag tag) {
        if (tag.contains("Name")) {
            name = tag.getString("Name");
        }
    }

    public void addConfiguration(PanelBlockEntity panel, int index) {
        addEditable(panel,"Name", index, () -> name, (s -> this.name = s));
    }


    public void addEditable(PanelBlockEntity panel, String name, int index, Supplier<String> read, Consumer<String> set) {
        panel.addEditableTaggedConfigTooltip(name, "widget-"+ index, read,set);
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
