package org.modogthedev.superposition.compat.cc;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.PortBehavior;

import java.util.ArrayList;
import java.util.List;

public class PortInterfacePeripheral implements IPeripheral {
    private final PortBehavior ports;

    public PortInterfacePeripheral(PortBehavior ports) {
        this.ports = ports;
    }

    @LuaFunction(mainThread = true)
    public List<CCSignal> getSignals(String port) {
        List<CCSignal> signals = new ArrayList<>();
        for (Signal portSignal : ports.getPortSignals(port)) {
            signals.add(new CCSignal(portSignal));
        }
        return signals;
    }

    @LuaFunction(mainThread = true)
    public CCSignal getSignal(String port) {
        List<Signal> portSignals = ports.getPortSignals(port);
        if (!portSignals.isEmpty()) {
            return new CCSignal(portSignals.getFirst());
        }
        return null;
    }

    @LuaFunction(mainThread = true)
    public void setSignal(String port, CCSignal signal) {
        ports.putPortSignals(port, List.of(signal.getSignal()));
    }

    @LuaFunction(mainThread = true)
    public void setSignals(String port, List<CCSignal> signals) {
        List<Signal> newSignals = new ArrayList<>();
        for (CCSignal signal : signals) {
            newSignals.add(signal.getSignal());
        }
        ports.putPortSignals(port,newSignals);
    }

    @Override
    public String getType() {
        return "port_interface";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (other instanceof PortInterfacePeripheral portInterfacePeripheral) {
            return ports.equals(portInterfacePeripheral.ports);
        }
        return false;
    }
}
