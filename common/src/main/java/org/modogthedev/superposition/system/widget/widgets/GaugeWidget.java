package org.modogthedev.superposition.system.widget.widgets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.widget.Widget;
import org.modogthedev.superposition.util.SignalHelper;

import java.util.List;

public class GaugeWidget extends Widget {
    private float minimum = 0;
    private float maximum = 1;

    private float value = 0;
    private float lastValue = value;

    public float getRenderNeedlePosition(float partialTick) {
        return Mth.map(Mth.lerp(partialTick,lastValue,value),minimum,maximum,0,1);
    }

    @Override
    public void tick(Level level, PanelBlockEntity panel) {
        lastValue = value;
        value = SignalHelper.getFloat(getPortSignals("value", panel));
    }

    @Override
    public PortConfig.Builder buildPorts(PortConfig.Builder builder) {
        return super.buildPorts(builder).addInputPort("value");
    }

    @Override
    public void write(CompoundTag tag) {
        super.write(tag);
        tag.putFloat("min",minimum);
        tag.putFloat("max",maximum);
        tag.putFloat("value",value);
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);
        if (tag.contains("max")) {
            minimum = tag.getFloat("min");
        }
        if (tag.contains("max")) {
            maximum = tag.getFloat("max");
        }
        if (tag.contains("value")) {
            value = tag.getFloat("value");
            lastValue = value;
        }
    }

    @Override
    public void addConfiguration(PanelBlockEntity panel) {
        super.addConfiguration(panel);
        panel.addEditableConfigTooltip("Minimum",() -> String.valueOf(minimum),(s -> {
            try {
                minimum = Float.parseFloat(s);
            } catch (NumberFormatException ignored) {}
        }
        ));

        panel.addEditableConfigTooltip("Maximum",() -> String.valueOf(maximum),(s -> {
            try {
                maximum = Float.parseFloat(s);
            } catch (NumberFormatException ignored) {}
        }
        ));
    }

    @Override
    public Vector3f getBounds() {
        return new Vector3f(6/16f,1/16f,6/16f);
    }
}
