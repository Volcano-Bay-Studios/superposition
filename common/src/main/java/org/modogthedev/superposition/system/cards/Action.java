package org.modogthedev.superposition.system.cards;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.modogthedev.superposition.core.SuperpositionActions;
import org.modogthedev.superposition.system.cards.actions.configuration.ActionConfiguration;

import java.util.ArrayList;
import java.util.List;

public abstract class Action implements Cloneable {
    private final Information info;
    private final List<ActionConfiguration> configurations = new ArrayList<>();
    private ResourceLocation selfReference = null;

    public Action(ResourceLocation action, Information info) {
        this.info = info;
    }

    public ResourceLocation getSelfReference() {
        if (selfReference == null) {
            selfReference = SuperpositionActions.ACTION.asVanillaRegistry().getKey(this);
        }
        return selfReference;
    }

    public Information getInfo() {
        return info;
    }

    public ItemStack getThumbnailItem() {
        return null;
    }

    public Action copy() {
        try {
            return (Action) clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public List<ActionConfiguration> getConfigurations() {
        return configurations;
    }

    public CompoundTag save(CompoundTag tag) {
        if (selfReference == null) {
            selfReference = SuperpositionActions.ACTION.asVanillaRegistry().getKey(this);
        }
        if (selfReference != null) {
            tag.putString("namespace", selfReference.getNamespace());
            tag.putString("path", selfReference.getPath());
            int i = 0;
            for (ActionConfiguration configuration : configurations) {
                i++;
            }
        }
        return tag;
    }

    public CompoundTag load(CompoundTag tag) {
        return tag;
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
