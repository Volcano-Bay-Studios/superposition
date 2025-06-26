package org.modogthedev.superposition.system.card.actions.configuration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionActions;

public class ActionConfiguration implements Cloneable {
    private final Component title;
    private ResourceLocation selfReference = null;

    public ActionConfiguration(Component title) {
        this.title = title;
    }

    public ResourceLocation getSelfReference() {
        if (selfReference == null) {
            selfReference = SuperpositionActions.ACTION_CONFIGURATIONS.asVanillaRegistry().getKey(this);
        }
        return selfReference;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int topBorder = Superposition.SUPERPOSITION_THEME.get("topBorder");
        guiGraphics.drawString(Minecraft.getInstance().font, title, 0, 0, topBorder);
    }

    public void mouse(int button, double x, double y) {

    }

    public ActionConfiguration copy() {
        try {
            return (ActionConfiguration) clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public int getHeight() {
        return 50;
    }

    public CompoundTag save(CompoundTag tag) {
        if (selfReference != null) {
            tag.putString("namespace",selfReference.getNamespace());
            tag.putString("path",selfReference.getPath());
        }
        return tag;
    }
    public CompoundTag load(CompoundTag tag) {
        return tag;
    }

    public static ActionConfiguration loadNew(CompoundTag tag) {
        ActionConfiguration configuration = SuperpositionActions.ACTION_CONFIGURATIONS.asVanillaRegistry().get(ResourceLocation.fromNamespaceAndPath(tag.getString("namespace"),tag.getString("path")));
        if (configuration != null) {
            configuration.load(tag);
        }
        return configuration;
    }
}
