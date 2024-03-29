package org.modogthedev.superposition.particle;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParticleManager {
    static HashMap<Level, List<Particle>> particles = new HashMap<>();

    public static void tick(TickEvent.LevelTickEvent event) {
        Level level = event.level;
        ifAbsent(level);
        if (!level.isClientSide) {
            for (Particle particle : particles.get(level)) {
                particle.tick();
            }
        }
    }

    private static void ifAbsent(Level level) {
        if (!particles.containsKey(level)) {
            particles.put(level, new ArrayList<>());
        }
    }

    public static void addParticle(Vec3 pos, Level level, float frequency, float quanta, Vec3 vel) {
        ifAbsent(level);
        particles.get(level).add(new Particle(pos, level, frequency, quanta, vel));
    }
}
