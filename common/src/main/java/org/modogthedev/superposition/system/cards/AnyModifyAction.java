package org.modogthedev.superposition.system.cards;

import org.modogthedev.superposition.system.signal.Signal;

public interface AnyModifyAction extends ExecutableAction {
    Signal modify(Signal ... signals);

    @Override
    default int getParameterCount() { return 6; }
}
