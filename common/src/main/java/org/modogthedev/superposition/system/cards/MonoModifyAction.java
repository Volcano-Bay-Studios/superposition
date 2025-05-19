package org.modogthedev.superposition.system.cards;

import org.modogthedev.superposition.system.signal.Signal;

public interface MonoModifyAction extends ExecutableAction {
    Signal modify(Signal signal);

    @Override
    default int getParameterCount() { return 1; }
}
