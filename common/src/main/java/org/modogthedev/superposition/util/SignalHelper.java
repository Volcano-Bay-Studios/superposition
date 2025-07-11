package org.modogthedev.superposition.util;

import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class SignalHelper {
    public static void updateSignalList(List<Signal> toFill , List<Signal> signals) {
        if (toFill.size() == signals.size()) {
            for (int i = 0; i < signals.size(); i++) {
                toFill.get(i).copy(signals.get(i));
            }
        } else if (toFill.size() > signals.size()) {
            ListIterator<Signal> iterator = toFill.listIterator();
            while (iterator.hasNext()) {
                int i = iterator.nextIndex();
                Signal signal = iterator.next();
                if (i >= signals.size()) {
                    iterator.remove();
                    continue;
                }
                signal.copy(signals.get(i));
            }
        } else {
            for (int i = 0; i < signals.size(); i++) {
                Signal signal = signals.get(i);
                if (i >= toFill.size()) {
                    toFill.add(new Signal(signal));
                    continue;
                }
                toFill.get(i).copy(signal);
            }
        }
    }

    public static ArrayList<Signal> listOf(Signal signal) {
        ArrayList<Signal> signals = new ArrayList<>();
        signals.add(signal);
        return signals;
    }

    private static boolean[] findIndexes(int n, int r) {
        boolean[] arrayWithObjects = new boolean[n];
        if (r < 2) {
            arrayWithObjects[7] = true;
            return arrayWithObjects;
        }

        int quotient = (n - 1) / (r - 1);
        int remainder = (n - 1) % (r - 1);

        int index = 0;
        do {
            arrayWithObjects[index] = true;
        } while ((index += quotient + (remainder-- > 0 ? 1 : 0)) < n);

        return arrayWithObjects;
    }

    public static Signal[] spaceArray(Signal[] signals, int size) {
        Signal[] signals1 = new Signal[size];
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (signals[(int) SuperpositionMth.getFromRange(size, 0, 12, 0, i)] != null)
                count++;
        }
        boolean[] booleans = findIndexes(size, count);
        int i = 0;
        for (int x = 0; x < size; x++) {
            if (booleans[x]) {
                signals1[x] = signals[i];
                i++;
            }
        }
        return signals1;
    }

    public static Signal randomSignal(List<Signal> signalList) {
        if (signalList == null || signalList.isEmpty()) {
            return null;
        }
        int ordinal = (int) Math.floor(Math.random() * signalList.size());
        return signalList.get(ordinal);
    }
}
