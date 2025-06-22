package org.modogthedev.superposition.screens.utils;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class ActionSpritesheet {

    public ResourceLocation spritesheetLocation;

    public HashMap<ResourceLocation, SpriteInformation> sprites = new HashMap<>();

    public int step = 0;

    public int scale;

    /**
     * @param resourceLocation
     * @param scale            the size of the sprite, the sprite must be a square and be a power of 2
     */
    public ActionSpritesheet(ResourceLocation resourceLocation, int scale) {
        spritesheetLocation = resourceLocation;
        this.scale = scale;
    }

    public void addSprite(ResourceLocation location) {
        int yStep = (int) Math.floor((double) step / (scale / 16d));
        int x = step * 16 - (yStep * 256);
        sprites.put(location, new SpriteInformation(x, yStep, 16, 16));
        step++;
    }

    public SpriteInformation get(ResourceLocation resourceLocation) {
        return sprites.get(resourceLocation);
    }

    public record SpriteInformation(int u1, int u2, int v1, int v2) {
    }
}
