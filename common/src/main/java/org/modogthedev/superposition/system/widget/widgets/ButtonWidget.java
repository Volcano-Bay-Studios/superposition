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

public class ButtonWidget extends Widget {
    private float position = 0;
    private float pressed = 0;
    private boolean canPlay = false;

    @Override
    public boolean tick(Level level, PanelBlockEntity panel, int index) {

        if (!level.isClientSide) {
            Signal signal = SignalHelper.getEmptySignal(level, panel.getBlockPos());
            signal.encode(pressed > 0);
            putPortSignals("value", List.of(signal), panel);
            if (pressed > 0) {
                pressed -= .4f;
                if (pressed <= 0) {
                    playSound(panel, SuperpositionSounds.BUTTON_UP.get(), 1f);
                } else if (canPlay) {
                    playSound(panel,SuperpositionSounds.BUTTON_DOWN.get(), 1f);
                    canPlay = false;
                }
                return true;
            } else {
                canPlay = true;
            }
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
        tag.putFloat("pressed", pressed);
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
        return new Vector3f(4 / 16f, 1 / 64f, 4 / 16f);
    }

    @Override
    public WidgetUseResult rightClickInteract(boolean alt, Level level, Vector3d hit) {
        if (!level.isClientSide) {
            pressed = 1;
        }
        return WidgetUseResult.HOLD;
    }
}
