package org.modogthedev.superposition.system.widget;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Something that holds widgets. Widgets only interact with the world through this, so they are not tied to a block.
 */
public interface WidgetHost {

    Level getLevel();

    /**
     * @return The position of the host in the world
     */
    Vec3 getWidgetPosition();

    /**
     * @return A new signal that originates from the host
     */
    Signal createSignal();

    List<Signal> getPortSignals(String port);

    boolean putPortSignals(String port, List<Signal> signals);

    void playSound(SoundEvent sound, float pitch);

    void addEditableTaggedConfigTooltip(String name, String tag, Supplier<String> read, Consumer<String> set);
}
