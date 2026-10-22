package org.modogthedev.superposition.system.widget.widgets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.modogthedev.superposition.core.SuperpositionSounds;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;
import org.modogthedev.superposition.system.sound.ClientMicrophone;
import org.modogthedev.superposition.system.stream.StreamManager;
import org.modogthedev.superposition.system.widget.Widget;
import org.modogthedev.superposition.system.widget.WidgetHost;
import org.modogthedev.superposition.util.WidgetUseResult;

import java.util.List;

public class MicrophoneWidget extends Widget {
    private boolean live = false;

    @Override
    public boolean tick(Level level, WidgetHost host, int index) {
        if (live) {
            Vec3 pos = host.getWidgetPosition();
            if (!level.isClientSide) {
                Signal signal = host.createSignal();
                signal.setEncodedData(EncodedData.of(getUuid(), 0));
                putPortSignals("audio", List.of(signal), host);
                StreamManager.markSource(level, getUuid(), pos);
            } else {
                ClientMicrophone.request(pos);
            }
        }
        return super.tick(level, host, index);
    }

    public boolean isLive() {
        return live;
    }

    @Override
    public PortConfig.Builder buildPorts(PortConfig.Builder builder) {
        return super.buildPorts(builder).addOutputPort("audio");
    }

    @Override
    public void write(CompoundTag tag) {
        super.write(tag);
        tag.putBoolean("live", live);
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        if (tag.contains("live")) {
            live = tag.getBoolean("live");
        }
    }

    @Override
    public Vector3f getBounds() {
        return new Vector3f(3 / 16f, 1 / 64f, 5 / 16f);
    }

    @Override
    public WidgetUseResult rightClickInteract(boolean alt, Level level, Vector3d hit) {
        live = !live;
        return WidgetUseResult.CLICK;
    }
}
