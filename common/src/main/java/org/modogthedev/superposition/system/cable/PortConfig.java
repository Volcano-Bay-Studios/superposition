package org.modogthedev.superposition.system.cable;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.modogthedev.superposition.system.cable.rope_system.AnchorConstraint;

import java.util.HashMap;

public class PortConfig {
    private HashMap<String, Port> ports = new HashMap<>();

    public HashMap<String, Port> getPorts() {
        return ports;
    }

    public static Builder create() {
        return new Builder(new PortConfig());
    }

    public Builder rebuild() {
        ports.clear();

        return new Builder(this);
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
        private final PortConfig config;

        public Builder(PortConfig config) {
            this.config = config;
        }

        public Builder addInputPort(String name) {
            Port port = new Port(name, IO.IN);
            config.ports.put(name, port);
            return this;
        }

        public Builder addOutputPort(String name) {
            Port port = new Port(name, IO.OUT);
            config.ports.put(name, port);
            return this;
        }

        public Builder addBothPort(String name) {
            Port port = new Port(name, IO.BOTH);
            config.ports.put(name, port);
            return this;
        }

        public boolean hasPort(String name) {
            return config.ports.containsKey(name);
        }

        public @Nullable Port getExisting(String name) {
            return config.ports.get(name);
        }

        public void removeExisting(String name) {
            config.ports.remove(name);
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
