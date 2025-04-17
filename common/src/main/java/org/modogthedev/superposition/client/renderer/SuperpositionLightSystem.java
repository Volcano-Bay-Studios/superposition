package org.modogthedev.superposition.client.renderer;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.Light;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SuperpositionLightSystem {
    public static HashMap<Level, HashMap<BlockPos, Light>> levelLightMap = new HashMap<>();
    public static HashMap<Level,List<BlockPos>> removablePositions = new HashMap<>();

    private static final LightRenderer renderer = VeilRenderSystem.renderer().getLightRenderer();


    public static void tick(Level level) {
        HashMap<BlockPos, Light> lightHashMap = levelLightMap.computeIfAbsent(level, (level1 -> new HashMap<>()));
        List<BlockPos> removable = removablePositions.computeIfAbsent(level, (level1 -> new ArrayList<>()));

        for (BlockPos pos : lightHashMap.keySet()) {
            if (!level.isLoaded(pos)) {
                removable.add(pos);
            }
        }

        for (BlockPos pos : removable) {
            Light light = lightHashMap.get(pos);
            if (light != null) {
                renderer.removeLight(light);
                lightHashMap.remove(pos);
            }
        }

        removable.clear();
        removable.addAll(lightHashMap.keySet());
    }

    public static void modifyLight(Level level,BlockPos pos, Light light) {
        HashMap<BlockPos, Light> lightHashMap = levelLightMap.computeIfAbsent(level, (level1 -> new HashMap<>()));
        List<BlockPos> removable = removablePositions.computeIfAbsent(level, (level1 -> new ArrayList<>()));

        if (!lightHashMap.containsKey(pos)) {
            renderer.addLight(light);
        }

        lightHashMap.put(pos,light);
        removable.remove(pos);
    }

    public static void clear() {
        levelLightMap.clear();
        removablePositions.clear();
    }
}
