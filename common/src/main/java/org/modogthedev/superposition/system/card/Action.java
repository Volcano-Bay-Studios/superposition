package org.modogthedev.superposition.system.card;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.core.SuperpositionActions;
import org.modogthedev.superposition.system.card.actions.configuration.ActionConfiguration;

import java.util.ArrayList;
import java.util.List;

public abstract class Action implements Cloneable {
    private final Information info;
    private List<ActionConfiguration> configurations = new ArrayList<>();
    private ResourceLocation selfReference = null;
    private Node node;

    public Action(ResourceLocation action, Information info) {
        this.info = info;
    }

    protected void setupConfigurations() {

    }

    public ResourceLocation getSelfReference() {
        if (selfReference == null) {
            selfReference = SuperpositionActions.ACTION.asVanillaRegistry().getKey(this);
        }
        return selfReference;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public Information getInfo() {
        return info;
    }

    public ItemStack getThumbnailItem() {
        return null;
    }

    public Action copy() {
        try {
            Action action = (Action) clone();
            action.configurations = new ArrayList<>();
            action.setupConfigurations();
            action.selfReference = getSelfReference();
            return action;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public List<ActionConfiguration> getConfigurations() {
        return configurations;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    public CompoundTag save(CompoundTag tag) {
        if (selfReference == null) {
            selfReference = SuperpositionActions.ACTION.asVanillaRegistry().getKey(this);
        }
        if (selfReference != null) {
            tag.putString("namespace", selfReference.getNamespace());
            tag.putString("path", selfReference.getPath());
            if (!configurations.isEmpty()) {
                tag.putBoolean("hasConfiguration", true);
                ListTag configurationsTag = new ListTag();
                for (ActionConfiguration configuration : configurations) {
                    if (configuration != null) {
                        configurationsTag.add(configuration.save(new CompoundTag()));
                    }
                }
                tag.put("configurations", configurationsTag);
            } else {
                tag.putBoolean("hasConfiguration", false);
            }
        }
        return tag;
    }

    public CompoundTag load(CompoundTag tag) {
        if (tag.getBoolean("hasConfiguration")) {
            configurations.clear();
            ListTag configurationsTag = tag.getList("configurations", 10);
            for (int i = 0; i < configurationsTag.size(); i++) {
                CompoundTag thisTag = configurationsTag.getCompound(i);
                configurations.add(ActionConfiguration.loadNew(thisTag));
            }
        }
        return tag;
    }

    public static Action loadNew(CompoundTag tag) {
        ResourceLocation selfReference = ResourceLocation.fromNamespaceAndPath(tag.getString("namespace"), tag.getString("path"));
        Action action = SuperpositionActions.ACTION.asVanillaRegistry().get(selfReference);
        if (action != null) {
            action.load(tag);
        }
        return action;
    }

    public enum Type {
        INPUT("Begins the data"),
        MODIFY("Changes a signals data in some way"),
        PERIPHERAL("Creates new data that can be used as instructions for a peripheral"),
        OUTPUT("Finalizes data"),
        OTHER("No information is available");

        private final String description;

        Type(String s) {
            description = s;
        }

        public String getDescription() {
            return description;
        }
    }

    public record Information(Component name, Component description, Type type) {
    }
}
