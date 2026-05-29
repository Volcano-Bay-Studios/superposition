package org.modogthedev.superposition.system.widget.widgets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.core.SuperpositionSounds;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.widget.Widget;
import org.modogthedev.superposition.util.SignalHelper;
import org.modogthedev.superposition.util.WidgetUseResult;

import java.util.List;

public class SwitchWidget extends Widget {
    private float position = 0;
    private float pressed = 0;

    @Override
    public boolean tick(Level level, PanelBlockEntity panel, int index) {
        if (!level.isClientSide) {
            Signal signal = SignalHelper.getEmptySignal(level, panel.getBlockPos());
            signal.encode(pressed > 0);
            putPortSignals("value", List.of(signal),panel);
        }
        if (pressed != position) {
            if (pressed == 1) {
                playSound(panel, SuperpositionSounds.SWITCH_ON.get(), (float) (Math.random()/10f+1f));
            } else {
                playSound(panel,SuperpositionSounds.SWITCH_OFF.get(), (float) (Math.random()/10f+1f));
            }
        }
        position = pressed;
        return super.tick(level, panel, index);
    }

    public float getPosition(float partialTicks) {
        return Mth.lerp(partialTicks, position, pressed);
    }

    @Override
    public PortConfig.Builder buildPorts(PortConfig.Builder builder) {
        return super.buildPorts(builder).addOutputPort("value");
    }

    @Override
    public void write(CompoundTag tag) {
        super.write(tag);
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
        return new Vector3f(3 / 16f, 1 / 16f, 4 / 16f);
    }

    @Override
    public WidgetUseResult rightClickInteract(boolean alt, Level level, Vector3d hit) {
        pressed = (pressed == 1) ? 0 : 1;
        return WidgetUseResult.CLICK;
    }
}
