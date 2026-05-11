package org.modogthedev.superposition.util;

import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;

public class SignalList {
    private List<Signal> current = new ArrayList<>();
    private List<Signal> old = new ArrayList<>();
    private int writeIndex = 0;

    // Write
    public boolean addAll(List<Signal> signals) {
        boolean value = false;
        for (Signal signal : signals) {
            if (add(signal)) {
                value = true;
            }
        }
        return value;
    }

    public boolean add(Signal signal) {
        boolean value = add(writeIndex, signal);
        writeIndex++;
        return value;
    }

    private boolean add(int index, Signal signal) {
        if (current.size() > index) {
            Signal signal1 = current.get(index);
            if (signal1.equals(signal)) {
                signal1.copy(signal);
                return false;
            }
            signal1.copy(signal);
            return true;
        } else {
            current.add(new Signal(signal));
        }
        return true;
    }

    // Read
    public List<Signal> getSignals() {
        return List.copyOf(old);
    }

    public @Nullable Signal get(int index) {
        if (old.size() > index) {
            return old.get(index);
        }
        return null;
    }

    public int size() {
        return old.size();
    }

    /**
     * This will move all signals into the old map. When interacting with a signal list, you may only read from old, and write to current.
     */
    public boolean flush() {
        boolean change = false;
        while (current.size() > writeIndex) {
            current.removeLast();
            change = true;
        }
        if (!old.equals(current)) {
            SignalHelper.updateSignalList(old, current);
            change = true;
        }
        writeIndex = 0;
        return change;
    }
}
