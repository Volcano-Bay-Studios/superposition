package org.modogthedev.superposition.system.cards;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class Action implements Cloneable {
    private final ResourceLocation selfReference;
    private final Information info;


    public Action(ResourceLocation action, Information info) {
        this.selfReference = action;
        this.info = info;
    }

    public ResourceLocation getSelfReference() {
        return this.selfReference;
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
