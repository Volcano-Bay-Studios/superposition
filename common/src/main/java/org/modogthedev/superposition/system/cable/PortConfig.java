package org.modogthedev.superposition.system.cable;

import org.joml.Vector2f;
import org.modogthedev.superposition.system.cable.rope_system.AnchorConstraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PortConfig {
    private List<Port> inputs = new ArrayList<>();
    private List<Port> outputs = new ArrayList<>();
    private HashMap<String, Port> all = new HashMap<>();

    public List<Port> getInputs() {
        return inputs;
    }

    public List<Port> getOutputs() {
        return outputs;
    }

    public HashMap<String, Port> getAll() {
        return all;
    }

    public static Builder create() {
        return new Builder();
    }

    public enum IO {
        IN,
        OUT,
        BOTH;
    }

    public static class Port {
        private final String name;
        private final IO io;

        public Port(String name, IO io) {
            this.name = name;
            this.io = io;
        }

        public String getName() {
            return name;
        }

        public IO getIO() {
            return io;
        }
    }

    public static class Builder {
        private final PortConfig config = new PortConfig();

        public Builder addInputPort(String name) {
            Port port = new Port(name, IO.IN);
            config.inputs.add(port);
            config.all.put(name, port);
            return this;
        }

        public Builder addOutputPort(String name) {
            Port port = new Port(name, IO.OUT);
            config.outputs.add(port);
            config.all.put(name, port);
            return this;
        }

        public Builder addBothPort(String name) {
            Port port = new Port(name, IO.BOTH);
            config.inputs.add(port);
            config.outputs.add(port);
            config.all.put(name, port);
            return this;
        }

        public PortConfig build() {
            return config;
        }
    }

    public static class ScreenCable {

        private final Cable cable;
        private final boolean isOut;
        private final Vector2f focusPosition = new Vector2f();
        private final Vector2f startPosition = new Vector2f();
        public String tempPort = null;
        public int ticksInTemp = 0;

        public ScreenCable(Cable cable, boolean isOut) {
            this.cable = cable;
            this.isOut = isOut;
        }

        public Cable getCable() {
            return cable;
        }

        public Vector2f getStartPosition() {
            return startPosition;
        }

        public boolean isOut() {
            return isOut;
        }

        public Vector2f getFocusPosition() {
            return focusPosition;
        }

        public String getBind() {
            if (isOut) {
                AnchorConstraint anchor = cable.getPoints().getFirst().getAnchor();
                if (anchor != null) {
                    return anchor.getPort();
                }
            } else {
                AnchorConstraint anchor = cable.getPoints().getLast().getAnchor();
                if (anchor != null) {
                    return anchor.getPort();
                }
            }
            return "";
        }

        public void bind(String name) {
            tempPort = name;
            ticksInTemp = 10;
            if (isOut) {
                AnchorConstraint anchor = cable.getPoints().getFirst().getAnchor();
                if (anchor != null) {
                    anchor.setPort(name);
                }
            } else {
                AnchorConstraint anchor = cable.getPoints().getLast().getAnchor();
                if (anchor != null) {
                    anchor.setPort(name);
                }
            }
        }
    }
}
