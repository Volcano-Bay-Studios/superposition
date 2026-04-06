package org.modogthedev.superposition.util;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public interface PortBehavior {
    /**
     * Retrieves the port config of this block. Usually this is created on startup and is returned whenever something needs it.
     * @return The port config of the block.
     */
    PortConfig getPortConfig();

    /**
     * Retrieves the signals from a specific port
     * @param port The port to retrieve the signals from
     * @return The signals in said port
     */
    @Unmodifiable
    List<Signal> getPortSignals(@Nullable String port);

    /**
     * Puts signals into a specific port on a block entity.
     * @param port The port to put the signals into
     * @param signals The signals to put in the port
     * @return True if the signals made any changes.
     */
    boolean putPortSignals(String port, List<Signal> signals);

    /**
     * The name that is set as the input of this block entity.
     *
     * @return The name of the in port.
     */
    default String inPortName() {
        return "in";
    }

    /**
     * The name that is set as the output of this block entity.
     * Note that changing this value will prevent the block from automatically pushing signals to the output side.
     *
     * @return The name of the out port.
     */
    default String outPortName() {
        return "out";
    }


    /**
     * Puts a single signal as a list into the output.
     *
     * @param signal The signal to output.
     */
    default void singleSignalOut(Signal signal) {
        putPortSignals(outPortName(), List.of(signal));
    }

    /**
     * Put a list of signals into the output port.
     *
     * @param signals The signals to output
     */
    default void signalsOut(List<Signal> signals) {
        putPortSignals(outPortName(), signals);
    }
}
