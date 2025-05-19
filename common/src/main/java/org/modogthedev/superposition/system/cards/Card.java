package org.modogthedev.superposition.system.cards;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.core.SuperpositionActions;

import java.util.HashMap;
import java.util.UUID;

public class Card { //TODO: make this work!

    private final HashMap<UUID, Node> nodes = new HashMap<>();

    public Card() {
        for (int i = 0; i < 6; i++) {
            nodes.put(UUID.randomUUID(), new Node(this));
        }
        float y = 70;
        for (Node node : nodes.values()) {
            node.getPosition().set(40,y);
            node.setTargetUUID((UUID) nodes.keySet().toArray()[0]);
            y += 30;
        }
    }

    public Card(CompoundTag tag) {
        load(tag);
    }


    public void save(CompoundTag pTag) {

    }

    public void load(CompoundTag pTag) {

    }

    public HashMap<UUID, Node> getNodes() {
        return nodes;
    }

    private Action getAction(ResourceLocation location) {
        return SuperpositionActions.ACTION.asVanillaRegistry().get(location);
    }
}
