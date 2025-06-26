package org.modogthedev.superposition.system.card;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.core.SuperpositionActions;
import org.modogthedev.superposition.core.SuperpositionItems;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Card { //TODO: make this work!

    private final List<Node> nodes = new ArrayList<>();
    public String title = "Card";

    public Card() {
    }

    public Card(CompoundTag tag) {
        load(tag);
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putString("title", title);
        ListTag nodesTag = new ListTag();
        for (Node node : nodes) {
            nodesTag.add(node.save(new CompoundTag()));
        }
        tag.put("nodes",nodesTag);
        return tag;
    }

    public CompoundTag load(CompoundTag tag) {
        try {
            title = tag.getString("title");
            ListTag nodesTag = tag.getList("nodes", 10);
            nodes.clear();
            for (int i = 0; i < nodesTag.size(); i++) {
                CompoundTag tag1 = nodesTag.getCompound(i);
                Node node = new Node(this);
                node.load(tag1);
                nodes.add(node);
            }
        } catch (Exception ignored) {}
        return tag;
    }

    public Attachment findAttachment(UUID target) {
        for (Node node : nodes) {
            for (Attachment attachment : node.getAttachments()) {
                if (attachment instanceof Attachment.InputAttachment inputAttachment && inputAttachment.getUuid().equals(target)) {
                    return attachment;
                }
            }
        }
        return null;
    }

    public ItemStack getItem() {
        return SuperpositionItems.CARD.get().create(this);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    private Action getAction(ResourceLocation location) {
        return SuperpositionActions.ACTION.asVanillaRegistry().get(location);
    }
}
