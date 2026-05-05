package org.modogthedev.superposition.compat.cc;

import dan200.computercraft.api.lua.LuaFunction;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;

public class CCSignal {
    private final Signal signal;

    public CCSignal(Signal signal) {
        this.signal = signal;
    }

    // Read
    @LuaFunction(mainThread = true)
    public final double getNumber() {
        EncodedData<?> encodedData = signal.getEncodedData();
        if (encodedData != null) {
            return encodedData.floatValue();
        }
        return 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean getBool() {
        EncodedData<?> encodedData = signal.getEncodedData();
        if (encodedData != null) {
            return encodedData.booleanValue();
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final String getString() {
        EncodedData<?> encodedData = signal.getEncodedData();
        if (encodedData != null) {
            return encodedData.stringValue();
        }
        return null;
    }

    @LuaFunction(mainThread = true)
    public final byte[] getBytes() {
        EncodedData<?> encodedData = signal.getEncodedData();
        if (encodedData != null) {
            return encodedData.byteArrayValue();
        }
        return null;
    }

    // Write
    @LuaFunction(mainThread = true)
    public final void setNumber(double value) {
        signal.setEncodedData(EncodedData.of((float) value));
    }

    @LuaFunction(mainThread = true)
    public final void setBool(boolean value) {
        signal.setEncodedData(EncodedData.of(value));
    }

    @LuaFunction(mainThread = true)
    public final void setString(String value) {
        signal.setEncodedData(EncodedData.of(value));
    }

    @LuaFunction(mainThread = true)
    public final void setBytes(byte[] value) {
        signal.setEncodedData(EncodedData.of(value));
    }

    // Special Read
    @LuaFunction(mainThread = true)
    public final double getFrequency() {
        return signal.getFrequency();
    }

    public Signal getSignal() {
        return signal;
    }
}
