package org.modogthedev.superposition.system.cards;

import org.modogthedev.superposition.system.signal.Signal;

public interface BiModifyAction extends ExecutableAction {
    Signal modify(Signal signal, Signal periphrealSignal);

    @Override
    default int getParameterCount() { return 2; }
}
