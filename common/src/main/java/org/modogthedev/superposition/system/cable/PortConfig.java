package org.modogthedev.superposition.system.cable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import org.joml.Vector2f;
import org.modogthedev.superposition.system.cable.rope_system.AnchorConstraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PortConfig {
    private List<Port> inputs = new ArrayList<>();
    private List<Port> outputs = new ArrayList<>();
    private HashMap<String, Port> all = new HashMap<>();
    private List<ScreenCable> screenCables = new ArrayList<>();

    public List<Port> getInputs() {
        return inputs;
    }

    public List<Port> getOutputs() {
        return outputs;
    }

    public HashMap<String, Port> getAll() {
        return all;
    }

    public List<ScreenCable> getScreenCables() {
        return screenCables;
    }

    public void remove(Cable cable) {
        for (ScreenCable screenCable : screenCables) {
            if (screenCable.cable.getId().equals(cable.getId())) {
                screenCables.remove(screenCable);
                return;
            }
        }
    }



    public void save(CompoundTag tag) {
        ListTag listTag = new ListTag();
        for (Port port : all.values()) {
            CompoundTag portCompoundTag = new CompoundTag();
            ListTag portTag = new ListTag();
            for (UUID connection : port.connections) {
                CompoundTag idTag = new CompoundTag();
                idTag.putUUID("uuid", connection);
                portTag.add(idTag);
            }
            portCompoundTag.putString("name", port.getName());
            portCompoundTag.put("connections", portTag);
            listTag.add(portCompoundTag);
        }
        ListTag cableTags = new ListTag();
        for (ScreenCable screenCable : screenCables) {
            CompoundTag cableTag = new CompoundTag();
            cableTag.putUUID("uuid", screenCable.cable.getId());
            cableTag.putBoolean("isOut", screenCable.isOut);
            cableTags.add(cableTag);
        }
        tag.put("cables",cableTags);
        tag.put("ports", listTag);
    }

    public void load(CompoundTag tag, Level level) {
        ListTag ports = tag.getList("ports", 10);
        for (int i = 0; i < ports.size(); i++) {
            CompoundTag compound = ports.getCompound(i);
            String name = compound.getString("name");
            ListTag list = compound.getList("connections", 10);
            Port port = all.get(name);
            if (port != null) {
                for (int j = 0; j < list.size(); j++) {
                    CompoundTag compound1 = list.getCompound(j);
                    port.connections.add(compound1.getUUID("uuid"));
                }
            }
        }
        ListTag cableList = tag.getList("cables", 10);
        for (int i = 0; i < cableList.size(); i++) {
            CompoundTag cableTag = cableList.getCompound(i);
            Cable cable = CableManager.getCable(level, cableTag.getUUID("uuid"));
            screenCables.add(new ScreenCable(cable,cableTag.getBoolean("isOut")));
        }
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
        private final List<UUID> connections = new ArrayList<>();

        public Port(String name, IO io) {
            this.name = name;
            this.io = io;
        }

        public String getName() {
            return name;
        }

        public List<UUID> getConnections() {
            return connections;
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
