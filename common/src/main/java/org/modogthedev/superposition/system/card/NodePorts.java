package org.modogthedev.superposition.system.card;

import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalList;

import java.util.*;

public class NodePorts {
    private final Map<String, SignalList> ports = new HashMap<>();
    private final List<String> visiblePorts = new ArrayList<>();

    protected NodePorts() {
    }

    public void putSignals(String s,List<Signal> signals) {
        if (ports.containsKey(s)) {
            ports.get(s).addAll(signals);
        } else {
            throw new ExecutionException("Tried to put "+signals.size()+" signals into port '"+s+"' which does not exist!");
        }
    }

    public void putSignal(String s,Signal signal) {
        if (ports.containsKey(s)) {
            ports.get(s).add(signal);
        } else {
            throw new ExecutionException("Tried to put signal into port '"+s+"' which does not exist!");
        }
    }

    public List<Signal> getSignals(String s) {
        if (ports.containsKey(s)) {
            return ports.get(s).getSignals();
        } else {
            throw new ExecutionException("Tried to get signals from port '"+s+"' which does not exist!");
        }
    }

    public int size() {
        return ports.size();
    }

    public Collection<String> getKeys() {
        return ports.keySet();
    }

    public Collection<String> getVisible() {
        return visiblePorts;
    }

    public void flush() {
        for (SignalList port : ports.values()) {
            port.flush();
        }
    }

    public static class Builder {
        private final NodePorts port = new NodePorts();

        public static Builder buildPorts() {
            return new Builder();
        }

        public Builder addPort(String key) {
            port.ports.put(key, new SignalList());
            port.visiblePorts.add(key);
            return this;
        }

        public Builder addVirtualPort(String key) {
            port.ports.put(key, new SignalList());
            return this;
        }

        public NodePorts build() {
            return port;
        }
    }
}
