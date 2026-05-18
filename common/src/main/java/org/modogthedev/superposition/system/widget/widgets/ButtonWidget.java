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

public class ButtonWidget extends Widget {
    private String color = "0xff0000";
    private float position = 0;
    private float pressed = 0;

    @Override
    public boolean tick(Level level, PanelBlockEntity panel, int index) {
        if (!level.isClientSide && pressed > 0) {
            pressed -= .2f;
            Signal signal = SignalHelper.getEmptySignal(level, panel.getBlockPos());
            signal.encode(pressed > 0);
            putPortSignals("value", List.of(signal),panel);
            return true;
        }
        position = pressed;
        return super.tick(level, panel, index);
    }

    public float getPosition(float partialTicks) {
        float lerp = Mth.lerp(partialTicks, position, pressed);
        if (lerp >= 0.51f) {
            position = 1;
            pressed = 1;
            return 1;
        }
        return lerp;
    }

    @Override
    public PortConfig.Builder buildPorts(PortConfig.Builder builder) {
        return super.buildPorts(builder).addOutputPort("value");
    }



    @Override
    public void write(CompoundTag tag) {
        super.write(tag);
        tag.putString("color", color);
        tag.putFloat("pressed",pressed);
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        if (tag.contains("pressed")) {
            pressed = tag.getFloat("pressed");
        }
    }


    @Override
    public Vector3f getBounds() {
        return new Vector3f(4 / 16f, 1 / 16f, 4 / 16f);
    }

    @Override
    public boolean rightClickInteract(boolean alt, Level level, Vector3f hit) {
        pressed = 1;
        return super.rightClickInteract(alt, level, hit);
    }
}
