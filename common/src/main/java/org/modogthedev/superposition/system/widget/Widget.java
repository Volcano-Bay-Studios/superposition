package org.modogthedev.superposition.system.widget;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import org.modogthedev.superposition.core.SuperpositionWidgets;

public class Widget implements Cloneable{
    private ResourceLocation location = null;

    private final Vector2i position = new Vector2i();

    public Widget() {
        super();
    }

    public ResourceLocation getLocation() {
        if (location == null) {
            location = SuperpositionWidgets.WIDGET.asVanillaRegistry().getKey(this);
        }
        return location;
    }

    public void setPosition(Vector2i position) {
        this.position.set(position);
    }

    public Vector2i getPosition() {
        return position;
    }

    public void write(CompoundTag tag) {

    }

    public void read(CompoundTag tag) {

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        getLocation();
        return super.clone();
    }

    public Widget makeClone() {
        try {
            Widget clone = (Widget) clone();
            clone.getPosition().set(position);
            return clone;
        } catch (CloneNotSupportedException ignored) {

        }
        return null;
    }
}
