package org.modogthedev.superposition.system.signal.data.types;

import org.modogthedev.superposition.system.signal.data.SignalData;

public class GenericData extends SignalData {
    public Object data;
    @Override
    public <T> T getData() {
        return (T) data;
    }

    @Override
    public <T> void setData(T data) {
        this.data = data;
    }
}
